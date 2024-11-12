package com.nju.software.download.consumer;

import com.alibaba.fastjson.JSON;
import com.nju.software.common.entity.CaseText;
import com.nju.software.common.utils.StringCompressUtil;
import com.nju.software.download.service.DownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @Description 消费者
 * @Author wxy
 * @Date 2024/4/3
 **/
@Component
@Slf4j
public class Consumer {
    private final DownloadService downloadService;

    @Autowired
    public Consumer(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    public void onMessage(String message) {
        // 解码
        String jsonString;
        try {
            jsonString = StringCompressUtil.decompress(message);
        } catch (Exception e) {
            log.error("解码失败");
            return;
        }
        // 反序列化
        CaseText caseText = JSON.parseObject(jsonString, CaseText.class);
        if (Objects.isNull(caseText)) {
            log.error("文书为空");
            return;
        }
        // 保存文书
        downloadService.storeDoc(List.of(caseText));

    }
}
