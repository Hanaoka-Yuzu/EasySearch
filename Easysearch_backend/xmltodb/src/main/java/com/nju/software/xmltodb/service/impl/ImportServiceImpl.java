package com.nju.software.xmltodb.service.impl;

import com.alibaba.fastjson.JSON;
import com.nju.software.common.utils.CodeEnum;
import com.nju.software.xmltodb.consumer.Consumer;
import com.nju.software.xmltodb.exception.XmltodbException;
import com.nju.software.xmltodb.service.ImportService;
import com.nju.software.xmltodb.utils.FileUtil;
import com.nju.software.xmltodb.utils.TimeUtil;
import com.nju.software.xmltodb.vo.DataVo;
import com.nju.software.xmltodb.vo.DatasetVo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static com.nju.software.common.utils.Constant.*;

/**
 * @Author wxy
 * @Date 2024/2/5
 **/
@Service
@Slf4j
@RefreshScope
public class ImportServiceImpl implements ImportService {

    private final Consumer consumer;
    private final FileUtil fileUtils;
    private final RocketMQTemplate rocketMQTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ReentrantLock pathReentrantLock = new ReentrantLock();
    private final ReentrantLock zipReentrantLock = new ReentrantLock();
    private BoundHashOperations<String, Object, Object> importHashOps;

    @Value("${rocketmq.topic.importData}")
    private String importDataTopic;

    @Value("${rocketmq.topic.causeCount}")
    private String causeCountTopic;

    @Value("${config.command}")
    private String command;

    @Autowired
    public ImportServiceImpl(Consumer consumer, FileUtil fileUtils, RocketMQTemplate rocketMQTemplate, StringRedisTemplate stringRedisTemplate) {
        this.consumer = consumer;
        this.fileUtils = fileUtils;
        this.rocketMQTemplate = rocketMQTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostConstruct
    public void initImportHashOps() {
        this.importHashOps = stringRedisTemplate.boundHashOps(IMPORT_KEY);
    }

    @Override
    public String uploadData(MultipartFile multipartFile) {
        // 拿不到锁不阻塞，直接结束，抛出异常
        if (!zipReentrantLock.tryLock()) {
            throw new XmltodbException(CodeEnum.TOO_MANY_UPLOAD_REQUEST.getMsg(), CodeEnum.TOO_MANY_UPLOAD_REQUEST.getCode());
        }

        // 校验文件是否为空
        if (Objects.isNull(multipartFile) || multipartFile.isEmpty()) {
            throw new XmltodbException(CodeEnum.UPLOAD_FILE_EMPTY.getMsg(), CodeEnum.UPLOAD_FILE_EMPTY.getCode());
        }

        try {
            // 获取zip压缩包文件
            File zipFile = fileUtils.convert(multipartFile);
            String datasetName = multipartFile.getOriginalFilename();
            String datasetId = datasetName + multipartFile.getSize();

            // 上传数据集
            upload(datasetId, datasetName, zipFile, ZIP, false);

            return datasetId;

        } catch (IOException ioException) {
            log.error("io出错", ioException);
            throw new RuntimeException(ioException);
        } finally {
            zipReentrantLock.unlock();
        }
    }

    @Override
    public String uploadData(String path, Boolean sync) {
        // 拿不到锁不阻塞，直接结束，抛出异常
        if (!pathReentrantLock.tryLock()) {
            throw new XmltodbException(CodeEnum.TOO_MANY_UPLOAD_REQUEST.getMsg(), CodeEnum.TOO_MANY_UPLOAD_REQUEST.getCode());
        }
        try {
            // 校验文件是否为空
            if (Objects.isNull(path) || path.isEmpty()) {
                throw new XmltodbException(CodeEnum.UPLOAD_FILE_EMPTY.getMsg(), CodeEnum.UPLOAD_FILE_EMPTY.getCode());
            }

            // 获取文件夹
            if (!Files.exists(Path.of(path))) {
                throw new XmltodbException(CodeEnum.UPLOAD_FILE_NOT_FOUND.getMsg(), CodeEnum.UPLOAD_FILE_NOT_FOUND.getCode());
            }
            File dataFolder = new File(path);
            String datasetName = dataFolder.getName();
            String datasetId = datasetName + dataFolder.length();

            // 上传数据集
            upload(datasetId, datasetName, dataFolder, PATH, sync);
            return datasetId;
        } finally {
            pathReentrantLock.unlock();
        }

    }


    @Override
    public List<DatasetVo> getUploadStatus() {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(IMPORT_KEY);
        return entries.keySet()
                .stream()
                .map(item -> JSON.parseObject((String) entries.get(item), DatasetVo.class))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(DatasetVo::getCreateTime))
                .toList();
    }

    @Override
    public DatasetVo getStatusByDatasetId(String datasetId) {
        if (Boolean.FALSE.equals(importHashOps.hasKey(datasetId))) {
            throw new XmltodbException(CodeEnum.DATASET_ID_NOT_FOUND.getMsg(), CodeEnum.DATASET_ID_NOT_FOUND.getCode());
        }
        DatasetVo datasetVo = JSON.parseObject((String) importHashOps.get(datasetId), DatasetVo.class);
        if (Objects.isNull(datasetVo)) {
            throw new XmltodbException(CodeEnum.DATASET_ID_NOT_FOUND.getMsg(), CodeEnum.DATASET_ID_NOT_FOUND.getCode());
        }
        return datasetVo;
    }

    @Override
    public void syncData() {
        try {
            // 执行 curl 命令
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Curl command executed successfully.");
            } else {
                log.error("Error executing curl command.");
            }
            rocketMQTemplate.convertAndSend(causeCountTopic, SYNC);
        } catch (IOException | InterruptedException e) {
            log.error("Error executing curl command.", e);
        }
    }

    private void upload(String datasetId, String datasetName, File file, String datasetType, Boolean sync) {
        // 当前数据集已经上传成功
        if (Boolean.TRUE.equals(importHashOps.hasKey(datasetId))) {
            DatasetVo existDataset = JSON.parseObject((String) importHashOps.get(datasetId), DatasetVo.class);
            if (Objects.nonNull(existDataset)) {
                if (Boolean.TRUE.equals(existDataset.getFinished())) {
                    throw new XmltodbException(CodeEnum.DATASET_ID_EXIST.getMsg(), CodeEnum.DATASET_ID_EXIST.getCode());
                }
                if (Boolean.FALSE.equals(existDataset.getError())) {
                    throw new XmltodbException(CodeEnum.DATASET_ID_PROCESSING.getMsg(), CodeEnum.DATASET_ID_PROCESSING.getCode());
                }
            }
        }

        // redis 插入数据集状态
        TimeZone beijingTimeZone = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(beijingTimeZone);
        DatasetVo datasetVo = DatasetVo.builder()
                .datasetId(datasetId)
                .datasetName(datasetName)
                .datasetType(datasetType)
                .createTime(TimeUtil.getTime())
                .finished(false)
                .error(false)
                .status(WAITING)
                .build();
        importHashOps.put(datasetId, JSON.toJSONString(datasetVo));

        // 发送消息处理数据集
        DataVo dataVo = DataVo.builder()
                .data(file)
                .datasetType(datasetType)
                .datasetId(datasetId)
                .build();

        if (sync) {
            consumer.onMessage(JSON.toJSONString(dataVo));
        } else {
            rocketMQTemplate.convertAndSend(importDataTopic, JSON.toJSONString(dataVo));
        }

    }


}
