package com.nju.software.xmltodb.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.nju.software.common.entity.*;
import com.nju.software.xmltodb.config.ThreadConfig;
import com.nju.software.xmltodb.entity.CaseTextContent;
import com.nju.software.xmltodb.mapper.*;
import com.nju.software.xmltodb.service.CaseService;
import com.nju.software.xmltodb.utils.Converter;
import com.nju.software.xmltodb.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.nju.software.common.utils.Constant.INDEX;

/**
 * @Author wxy
 * @Date 2024/3/5
 **/
@Service
@Slf4j
public class CaseServiceImpl implements CaseService {

    private final CaseBasicInfoMapper caseBasicInfoMapper;
    private final CaseCauseMapper caseCauseMapper;
    private final CaseLitigantMapper caseLitigantMapper;
    private final CaseTextContentMapper caseTextContentMapper;
    private final CaseApplicableLawMapper caseApplicableLawMapper;
    private final CaseJudiciaryMemberMapper caseJudiciaryMemberMapper;
    private final FileUtil fileUtils;
    private final ThreadConfig threadConfig;
    private final RestHighLevelClient client;
    @Qualifier("computeTaskExecutor")
    private final Executor computeTaskExecutor;
    @Value("${config.batchSize.upload}")
    private int uploadBatchSize;
    @Value("${config.batchSize.text}")
    private int textBatchSize;
    @Value("${config.batchSize.parse}")
    private int parseBatchSize;

    @Autowired
    public CaseServiceImpl(FileUtil fileUtils,
                           CaseBasicInfoMapper caseBasicInfoMapper,
                           CaseCauseMapper caseCauseMapper,
                           CaseLitigantMapper caseLitigantMapper, CaseTextContentMapper caseTextContentMapper,
                           CaseApplicableLawMapper caseApplicableLawMapper,
                           CaseJudiciaryMemberMapper caseJudiciaryMemberMapper,
                           ThreadConfig threadConfig, RestHighLevelClient client,
                           Executor computeTaskExecutor) {
        this.fileUtils = fileUtils;
        this.caseBasicInfoMapper = caseBasicInfoMapper;
        this.caseCauseMapper = caseCauseMapper;
        this.caseLitigantMapper = caseLitigantMapper;
        this.caseTextContentMapper = caseTextContentMapper;
        this.caseApplicableLawMapper = caseApplicableLawMapper;
        this.caseJudiciaryMemberMapper = caseJudiciaryMemberMapper;
        this.threadConfig = threadConfig;
        this.client = client;
        this.computeTaskExecutor = computeTaskExecutor;
    }

    public List<CaseInfo> parse(List<File> fileList) {
        List<CompletableFuture<List<CaseInfo>>> futureList = Lists.newArrayList();
        int batchSize = getParseBatchSize(fileList.size());
        List<List<File>> lists = Lists.partition(fileList, batchSize);
        int total = lists.size();
        for (int i = 0; i < total; i++) {
            final int y = i;
            CompletableFuture<List<CaseInfo>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return fileUtils.parse(lists.get(y));
                } catch (Exception e) {
                    log.error("解析文件失败: {}", e.getMessage());
                    return Lists.newArrayList();
                }

            }, computeTaskExecutor);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(CompletableFuture[]::new)).join();

        List<CaseInfo> caseInfoList = futureList.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .filter(caseInfo -> Objects.nonNull(caseInfo.getCaseOrder()))
                .collect(Collectors.toList());
        log.info("解析完成，共计{}条数据", caseInfoList.size());
        return caseInfoList;
    }

    @Override
    public Integer uploadMySQLData(List<CaseInfo> caseInfoList) {
        List<List<CaseInfo>> lists = Lists.partition(caseInfoList, uploadBatchSize);
        int importCount = 0;
        for (List<CaseInfo> list : lists) {
            try {
                batchInsert(list);
                importCount += list.size();
                String progress = String.format("%.2f", importCount * 1.0 / caseInfoList.size());
                log.info("当前线程导入数据: {}, 进度: {}", importCount, progress);
            } catch (Exception e) {
                log.error("导入数据失败: {}", e.getMessage());
            }
        }
        log.info("导入所有数据, 总计{}条", importCount);
        return importCount;
    }

    @Override
    public Integer uploadESData(List<CaseInfo> caseInfoList) {
        List<String> jsonDataList = caseInfoList
                .stream()
                .map(Converter::convert)
                .map(JSON::toJSONString)
                .toList();
        List<List<String>> lists = Lists.partition(jsonDataList, uploadBatchSize);
        AtomicInteger count = new AtomicInteger(0);
        lists.forEach(list -> {
            batchUpload(list);
            count.addAndGet(list.size());
            log.info("当前导入数据: {}, 进度: {}%", count.get(), count.get() * 100.0 / jsonDataList.size());
        });
        return jsonDataList.size();
    }

    /**
     * 获取最近解析batchSize
     *
     * @param size 文件数量
     * @return batchSize
     */
    private int getParseBatchSize(int size) {
        int batchSize = size / threadConfig.getComputeThreads();
        if (batchSize < parseBatchSize) {
            batchSize = parseBatchSize;
        }
        return batchSize;
    }

    /**
     * 批量插入数据
     *
     * @param caseInfoList 数据
     */
    protected void batchInsert(List<CaseInfo> caseInfoList) {
        List<CaseBasicInfo> basicInfoList = caseInfoList
                .stream()
                .map(CaseInfo::getCaseBasicInfo)
                .filter(Objects::nonNull)
                .filter(caseBasicInfo -> Objects.nonNull(caseBasicInfo.getCaseOrder()))
                .toList();
        List<CaseCause> causeList = caseInfoList
                .stream()
                .map(CaseInfo::getCaseCauseList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(caseCause -> Objects.nonNull(caseCause.getCaseOrder()))
                .toList();
        List<CaseApplicableLaw> applicableLawList = caseInfoList
                .stream()
                .map(CaseInfo::getCaseApplicableLawList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(caseApplicableLaw -> Objects.nonNull(caseApplicableLaw.getCaseOrder()))
                .toList();
        List<CaseJudiciaryMember> judiciaryMemberList = caseInfoList
                .stream()
                .map(CaseInfo::getCaseJudiciaryMemberList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(caseJudiciaryMember -> Objects.nonNull(caseJudiciaryMember.getCaseOrder()))
                .toList();
        List<CaseLitigant> litigantList = caseInfoList
                .stream()
                .map(CaseInfo::getCaseLitigantList)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(caseLitigant -> Objects.nonNull(caseLitigant.getCaseOrder()))
                .toList();
        List<CaseTextContent> caseTextContentList = caseInfoList
                .stream()
                .map(CaseInfo::getCaseText)
                .map(CaseTextContent::new)
                .filter(caseTextContent -> Objects.nonNull(caseTextContent.getCaseOrder()))
                .toList();

        if (!basicInfoList.isEmpty()) {
            caseBasicInfoMapper.insertBatchSomeColumn(basicInfoList);
        }

        if (!causeList.isEmpty()) {
            caseCauseMapper.insertBatchSomeColumn(causeList);
        }

        if (!litigantList.isEmpty()) {
            caseLitigantMapper.insertBatchSomeColumn(litigantList);
        }

        if (!applicableLawList.isEmpty()) {
            caseApplicableLawMapper.insertBatchSomeColumn(applicableLawList);
        }

        if (!judiciaryMemberList.isEmpty()) {
            caseJudiciaryMemberMapper.insertBatchSomeColumn(judiciaryMemberList);
        }

        if (!caseTextContentList.isEmpty()) {
            List<List<CaseTextContent>> lists = Lists.partition(caseTextContentList, textBatchSize);
            lists.forEach(caseTextContentMapper::insertBatchSomeColumn);
        }
    }

    public void batchUpload(List<String> jsonDataList) {
        BulkRequest bulkRequest = new BulkRequest();

        for (String jsonData : jsonDataList) {
            IndexRequest indexRequest = new IndexRequest(INDEX).source(jsonData, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (bulkResponse.hasFailures()) {
                throw new RuntimeException("Bulk insert failed: " + bulkResponse.buildFailureMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
