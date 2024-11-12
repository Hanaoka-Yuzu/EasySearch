package com.nju.software.download.service.impl;

import com.nju.software.common.entity.CaseText;
import com.nju.software.download.feign.FeignService;
import com.nju.software.download.service.DownloadService;
import com.nju.software.download.utils.FileUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RefreshScope
public class DownloadServiceImpl implements DownloadService {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private FeignService feignService;

    @Override
    public Integer storeDoc(List<CaseText> caseTextList) {
        return fileUtil.storeDoc(caseTextList);
    }

    private static final List<String> CASE_ORDER_LIST = List.of(
            "******中级**** 刑事裁定书 （2015）*刑终字第***号",
            "******中级**** 刑事裁定书 （2014）**刑终字第***号",
            "上海市浦东新区人民法院 民事判决书 （2014）浦民一（民）初字第16263号");

    @Override
    public void downloadDoc(HttpServletResponse response, String fileName, String userID) {
//        String requestId = fileName + userID;
//        Boolean isDuplicate = redisTemplate.opsForValue().setIfPresent("request:" + requestId, "1");
//        if (isDuplicate) return Result.ok().message("Duplicated request");
//        redisTemplate.opsForValue().set("request:" + requestId, "1", 1, TimeUnit.MINUTES);
        fileUtil.downloadFile(response, fileName);
    }

    @Override
    public void batchDownloadDoc(HttpServletResponse response, List<String> caseOrders, String userID) {
        ;
//        String requestId = caseOrders.toString() + userID;
//        Boolean isDuplicate = redisTemplate.opsForValue().setIfPresent("request:" + requestId, "1");
//        if (isDuplicate) return Result.ok().message("Duplicated request");
//        redisTemplate.opsForValue().set("request:" + requestId, "1", 1, TimeUnit.MINUTES);
        fileUtil.batchDownloadFile(response, caseOrders);
    }
}
