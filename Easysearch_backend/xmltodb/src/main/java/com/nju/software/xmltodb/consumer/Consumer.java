package com.nju.software.xmltodb.consumer;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.nju.software.common.entity.CaseInfo;
import com.nju.software.common.entity.CaseText;
import com.nju.software.common.utils.CodeEnum;
import com.nju.software.common.utils.StringCompressUtil;
import com.nju.software.xmltodb.exception.XmltodbException;
import com.nju.software.xmltodb.service.CaseService;
import com.nju.software.xmltodb.utils.FileUtil;
import com.nju.software.xmltodb.utils.TimeUtil;
import com.nju.software.xmltodb.vo.DataVo;
import com.nju.software.xmltodb.vo.DatasetVo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static com.nju.software.common.utils.CodeEnum.DATASET_ID_NOT_FOUND;
import static com.nju.software.common.utils.Constant.*;


/**
 * @Description 消息队列消费者
 * @Author wxy
 * @Date 2024/3/8
 **/
@Component
@Slf4j
@RefreshScope
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group}", topic = "${rocketmq.topic.importData}")
public class Consumer implements RocketMQListener<String> {
    private final CaseService caseService;
    private final RocketMQTemplate rocketMQTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final FileUtil fileUtil;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private BoundHashOperations<String, Object, Object> importHashOps;

    @Value("${rocketmq.topic.saveDoc}")
    private String saveDocTopic;

    @Value("${config.batchSize.save}")
    private int saveBatchSize;

    @Value("${config.batchSize.limit}")
    private int limit;

    @Value("${config.interval}")
    private int interval;

    @Autowired
    public Consumer(CaseService caseService, RocketMQTemplate rocketMQTemplate, StringRedisTemplate stringRedisTemplate, FileUtil fileUtil) {
        this.caseService = caseService;
        this.rocketMQTemplate = rocketMQTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.fileUtil = fileUtil;
    }

    @PostConstruct
    public void initImportHashOps() {
        this.importHashOps = stringRedisTemplate.boundHashOps(IMPORT_KEY);
    }


    @Override
    public void onMessage(String s) {
        try {
            // 控制并发处理消息
            if (!reentrantLock.tryLock()) {
                throw new XmltodbException(CodeEnum.TOO_MANY_UPLOAD_REQUEST.getMsg(), CodeEnum.TOO_MANY_UPLOAD_REQUEST.getCode());
            }

            // 反序列化对象
            DataVo dataVo = JSON.parseObject(s, DataVo.class);
            String datasetId = dataVo.getDatasetId();
            File data = dataVo.getData();
            DatasetVo datasetVo = JSON.parseObject((String) importHashOps.get(datasetId), DatasetVo.class);
            if (Objects.isNull(datasetVo)) {
                throw new XmltodbException(DATASET_ID_NOT_FOUND.getMsg(), DATASET_ID_NOT_FOUND.getCode());
            }
            if (Boolean.TRUE.equals(datasetVo.getFinished())) {
                return;
            }
            log.info("处理消息: {}", datasetVo.getDatasetId());

            // 设置北京时区
            TimeZone beijingTimeZone = TimeZone.getTimeZone("Asia/Shanghai");
            TimeZone.setDefault(beijingTimeZone);

            // 获取 xml 文件
            datasetVo.setFinished(Boolean.FALSE);
            datasetVo.setError(Boolean.FALSE);
            datasetVo.setErrorMsg(null);
            datasetVo.setUpdateTime(TimeUtil.getTime());
            datasetVo.setStatus(TRAVERSE);
            importHashOps.put(datasetId, JSON.toJSONString(datasetVo));
            List<File> fileList = List.of();
            if (ZIP.equals(datasetVo.getDatasetType())) {
                fileList = fileUtil.dealZip(data);
            } else if (PATH.equals(datasetVo.getDatasetType())) {
                fileList = fileUtil.traverseFolder(data);
            }
            log.info("获取文件列表成功");

            // 限制数据量, 超过限制需要分批导入
            log.info("文件数量: {}", fileList.size());
            List<List<File>> partition = Lists.partition(fileList, limit);
            partition.forEach(
                    list -> {
                        // 多线程解析
                        datasetVo.setStatus(PARSE);
                        datasetVo.setUpdateTime(TimeUtil.getTime());
                        datasetVo.setTotalCount(list.size());
                        importHashOps.put(datasetId, JSON.toJSONString(datasetVo));
                        List<CaseInfo> caseInfoList = caseService.parse(list);
                        log.info("解析成功");
                        // 导入数据
                        datasetVo.setStatus(UPLOAD);
                        datasetVo.setUpdateTime(TimeUtil.getTime());
                        importHashOps.put(datasetId, JSON.toJSONString(datasetVo));
                        Integer uploadSize = caseService.uploadESData(caseInfoList);
                        datasetVo.setUploadCount(uploadSize);
                        log.info("导入数据库成功");
                        // 保存文件
                        datasetVo.setStatus(SAVE);
                        datasetVo.setUpdateTime(TimeUtil.getTime());
                        importHashOps.put(datasetId, JSON.toJSONString(datasetVo));
                        List<CaseText> caseTextList = caseInfoList.stream().map(CaseInfo::getCaseText).toList();
                        saveDocByMessage(caseTextList);
                        datasetVo.setSaveCount(caseTextList.size());
                        log.info("保存文件成功");

                        if (partition.size() > 1) {
                            log.info("当前{}条数据已保存", limit);
                        }
                    }
            );


            // 成功上传数据集并保存文件
            datasetVo.setStatus(FINISHED);
            datasetVo.setUpdateTime(TimeUtil.getTime());
            datasetVo.setError(Boolean.FALSE);
            datasetVo.setErrorMsg(null);
            datasetVo.setFinished(Boolean.TRUE);

            importHashOps.put(datasetId, JSON.toJSONString(datasetVo));
            log.info("数据集处理完成: {}", datasetVo.getDatasetId());
        } catch (Exception e) {
            DataVo dataVo = JSON.parseObject(s, DataVo.class);
            String datasetId = dataVo.getDatasetId();
            DatasetVo datasetVo = JSON.parseObject((String) importHashOps.get(datasetId), DatasetVo.class);
            if (Objects.nonNull(datasetVo)) {
                datasetVo.setError(Boolean.TRUE);
                datasetVo.setErrorMsg(e.getMessage());
                datasetVo.setUpdateTime(TimeUtil.getTime());
                if (TRAVERSE.equals(datasetVo.getStatus())) {
                    datasetVo.setStatus(TRAVERSE_FAILED);
                } else if (PARSE.equals(datasetVo.getStatus())) {
                    datasetVo.setStatus(PARSE_FAILED);
                } else if (UPLOAD.equals(datasetVo.getStatus())) {
                    datasetVo.setStatus(UPLOAD_FAILED);
                } else if (SAVE.equals(datasetVo.getStatus())) {
                    datasetVo.setStatus(SAVE_FAILED);
                } else if (SYNC.equals(datasetVo.getStatus())) {
                    datasetVo.setStatus(SYNC_FAILED);
                }
                importHashOps.put(datasetId, JSON.toJSONString(datasetVo));
            }
            throw new RuntimeException(e);
        } finally {
            reentrantLock.unlock();
        }


    }

    private void saveDocByMessage(List<CaseText> caseTextList) {
        try {
            int size = caseTextList.size();
            List<List<CaseText>> batchList = Lists.partition(caseTextList, saveBatchSize);
            AtomicInteger atomicInteger = new AtomicInteger(0);
            for (List<CaseText> list : batchList) {
                List<String> caseTexts = list.stream()
                        .map(JSON::toJSONString)
                        .map(StringCompressUtil::compress)
                        .toList();
                caseTexts.forEach(caseText -> {
                    atomicInteger.addAndGet(1);
                    send(caseText);
                });
                log.info("已发送 {} 条数据, 进度: {}%", atomicInteger.get(), atomicInteger.get() * 100.0 / size);
                Thread.sleep(interval);
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(String caseText) {
        try {
            rocketMQTemplate.convertAndSend(saveDocTopic, caseText);
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage());
        }
    }


}
