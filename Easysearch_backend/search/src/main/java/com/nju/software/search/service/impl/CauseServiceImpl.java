package com.nju.software.search.service.impl;

import com.nju.software.common.utils.CodeEnum;
import com.nju.software.search.config.UpdateBatchWrapper;
import com.nju.software.search.entity.CauseNode;
import com.nju.software.search.entity.CauseNodeLink;
import com.nju.software.search.exception.SearchException;
import com.nju.software.search.mapper.CauseNodeLinkMapper;
import com.nju.software.search.mapper.CauseNodeMapper;
import com.nju.software.search.service.CauseService;
import com.nju.software.search.utils.ElasticSearchUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.nju.software.common.utils.Constant.*;

/**
 * @Author wxy
 * @Date 2024/3/15
 **/
@Service
@Slf4j
@RefreshScope
public class CauseServiceImpl implements CauseService {

    @Value("${config.excelPath}")
    private String excelPath;
    private final StringRedisTemplate stringRedisTemplate;
    private BoundHashOperations<String, Object, Object> causeCountHashOps;
    private final RestHighLevelClient client;
    private final ElasticSearchUtil elasticSearchUtil;
    private final CauseNodeMapper causeNodeMapper;
    private final CauseNodeLinkMapper causeNodeLinkMapper;

    @Autowired
    public CauseServiceImpl(StringRedisTemplate stringRedisTemplate, RestHighLevelClient client, ElasticSearchUtil elasticSearchUtil, CauseNodeMapper causeNodeMapper, CauseNodeLinkMapper causeNodeLinkMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.client = client;
        this.elasticSearchUtil = elasticSearchUtil;
        this.causeNodeMapper = causeNodeMapper;
        this.causeNodeLinkMapper = causeNodeLinkMapper;
    }

    @PostConstruct
    public void initCauseCountHashOps() {
        this.causeCountHashOps = stringRedisTemplate.boundHashOps(CAUSE_COUNT_KEY);
    }

    @Override
    @Transactional
    public void analyseExcel(MultipartFile excelFile) {
        if (Objects.isNull(excelFile) || excelFile.isEmpty()) {
            throw new SearchException(CodeEnum.EXCEL_FILE_EMPTY.getMsg(), CodeEnum.EXCEL_FILE_EMPTY.getCode());
        }
        String filename = excelFile.getOriginalFilename();
        if (Objects.isNull(filename) || !filename.endsWith(".xlsx")) {
            throw new SearchException(CodeEnum.EXCEL_FILE_FORMAT_ERROR.getMsg(), CodeEnum.EXCEL_FILE_FORMAT_ERROR.getCode());
        }


        try {
            File file = new File(excelPath + filename);
            excelFile.transferTo(file);
            Workbook workbook = WorkbookFactory.create(file);
            Iterator<Sheet> iterable = workbook.sheetIterator();
            List<CauseNode> causeNodeList = new ArrayList<>();
            // 解析 excel
            while (iterable.hasNext()) {
                Sheet sheet = iterable.next();
                for (int i = 1; i < sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    String caseCategory = row.getCell(0).getStringCellValue();
                    Integer causeCode = (int) row.getCell(1).getNumericCellValue();
                    Integer parentCauseCode = i == 1 ? -1 : (int) row.getCell(2).getNumericCellValue();
                    String cause = row.getCell(3).getStringCellValue().strip();
                    CauseNode causeNode = CauseNode.builder()
                            .causeCategory(caseCategory)
                            .cause(cause)
                            .causeCode(causeCode)
                            .parentCauseCode(parentCauseCode)
                            .build();
                    causeNodeList.add(causeNode);
                }

            }
            // 导入数据库
            causeNodeMapper.insertBatchSomeColumn(causeNodeList);

            // 关闭文件
            workbook.close();


        } catch (IOException e) {
            log.error("io出错", e);
            throw new RuntimeException(e);
        }


    }

    @Override
    public void updateNodeInfo() {
        List<CauseNode> causeNodeList = causeNodeMapper.selectList(null);
        Map<Integer, CauseNode> causeNodeMap = causeNodeList.stream()
                .peek(e -> e.setLayer(-1))
                .collect(Collectors.toMap(CauseNode::getCauseCode, e -> e));
        Map<Integer, Set<CauseNode>> childrenMap = new HashMap<>();
        int count = 0;
        int size = causeNodeList.size();
        // 遍历获取直接子节点
        for (CauseNode causeNode : causeNodeList) {
            if (causeNode.getParentCauseCode() == -1) {
                causeNodeMap.get(causeNode.getCauseCode()).setLayer(0);
                count++;
                continue;
            }
            if (childrenMap.containsKey(causeNode.getParentCauseCode())) {
                childrenMap.get(causeNode.getParentCauseCode()).add(causeNode);
            } else {
                Set<CauseNode> hashSet = new HashSet<>();
                hashSet.add(causeNode);
                childrenMap.put(causeNode.getParentCauseCode(), hashSet);
            }
        }
        List<CauseNodeLink> causeNodeLinkList = new ArrayList<>();
        childrenMap.forEach((k, v) -> v.forEach(i -> {
            CauseNodeLink causeNodeLink = CauseNodeLink.builder()
                    .causeCode(k)
                    .childrenCauseCode(i.getCauseCode())
                    .childrenCause(i.getCause())
                    .build();
            causeNodeLinkList.add(causeNodeLink);
        }));
        causeNodeLinkMapper.deleteByMap(null);
        causeNodeLinkMapper.insertBatchSomeColumn(causeNodeLinkList);
        // 更新层级信息
        int depth = 10;
        for (int i = 1; i < depth && count < size; i++) {
            for (CauseNode node : causeNodeList) {
                CauseNode parentNode = causeNodeMap.get(node.getParentCauseCode());
                if (Objects.nonNull(parentNode) && parentNode.getLayer() == i - 1) {
                    node.setLayer(i);
                    count++;
                }

            }
        }
        causeNodeMapper.updateBatchById(causeNodeList, new UpdateBatchWrapper<CauseNode>().setUpdateFields(CauseNode::getLayer));
        // 设置最高深度
        AtomicInteger maxDepth = new AtomicInteger(-1);
        causeNodeList.forEach(node-> maxDepth.set(Math.max(maxDepth.get(), node.getLayer())));
        stringRedisTemplate.opsForValue().set(CAUSE_MAX_DEPTH, String.valueOf(maxDepth.get()));

    }


    @Override
    public void syncCauseCountData() {
        // 查询所有案由
        Map<String, Integer> causeCountMap = new HashMap<>();
        List<CauseNode> causeList = causeNodeMapper.selectList(null)
                .stream()
                .sorted(Comparator.comparing(CauseNode::getCauseCode))
                .distinct()
                .toList();
        List<String> causeNameList = causeList.stream().map(CauseNode::getCause).toList();
        Map<Integer, String> causeCodeNameMap = causeList.stream().collect(Collectors.toMap(CauseNode::getCauseCode, CauseNode::getCause));

        // 使用 es 获取所有案由的统计（只自身，无子案由）
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        TermsAggregationBuilder builder = AggregationBuilders.terms(COMPLETE_CAUSE).field(COMPLETE_CAUSE + KEYWORD);
        searchSourceBuilder.aggregation(AggregationBuilders.nested(CASE_CAUSE, CASE_CAUSE).subAggregation(builder));
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedNested causeNested = aggregations.get(CASE_CAUSE);
            Aggregations nestedAggregations = causeNested.getAggregations();
            Map<String, Long> countMap = elasticSearchUtil.countMap(nestedAggregations.get(COMPLETE_CAUSE));
            causeNameList.forEach(e -> {
                if (countMap.containsKey(e)) {
                    causeCountMap.put(e, countMap.get(e).intValue());
                } else {
                    causeCountMap.put(e, 0);
                }
            });
        } catch (IOException e) {
            log.error("es查询出错", e);
            throw new RuntimeException(e);
        }

        // 获取子案由
        Map<Integer, List<String>> causeNodeLinkCodeMap = new HashMap<>();
        List<CauseNodeLink> causeNodeLinkList = causeNodeLinkMapper.selectList(null);
        causeNodeLinkList.forEach(e -> {
            if (causeNodeLinkCodeMap.containsKey(e.getCauseCode())) {
                causeNodeLinkCodeMap.get(e.getCauseCode()).add(e.getChildrenCause());
            } else {
                List<String> list = new ArrayList<>();
                list.add(e.getChildrenCause());
                causeNodeLinkCodeMap.put(e.getCauseCode(), list);
            }
        });
        Map<String, List<String>> causeNodeLinkMap = new HashMap<>();
        causeNodeLinkCodeMap.forEach((key, value) -> causeNodeLinkMap.put(causeCodeNameMap.get(key), value));

        // 递归更新案由统计
        int maxDepth = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(CAUSE_MAX_DEPTH)));
        for (int i = maxDepth; i >= 0; i--) {
            for (CauseNode causeNode : causeList) {
                if (causeNode.getLayer() == i) {
                    String cause = causeNode.getCause();
                    int count = causeCountMap.getOrDefault(cause, 0);
                    List<String> childrenCauseList = causeNodeLinkMap.getOrDefault(cause, new ArrayList<>());
                    for (String childrenCause : childrenCauseList) {
                        count += causeCountMap.getOrDefault(childrenCause, 0);
                    }
                    causeCountMap.put(cause, count);
                }
            }
        }
        Map<String, String> resultMap = new HashMap<>();
        causeCountMap.forEach((k, v) -> {
            String causeName = k.strip();
            String count = String.valueOf(v);
            resultMap.put(causeName, count);
        });
        causeCountHashOps.putAll(resultMap);
    }


}
