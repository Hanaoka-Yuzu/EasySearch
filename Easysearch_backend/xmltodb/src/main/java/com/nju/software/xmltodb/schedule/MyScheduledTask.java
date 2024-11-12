package com.nju.software.xmltodb.schedule;


import com.nju.software.xmltodb.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Description 定时任务，同步 mysql 数据到 es
 * @Author wxy
 * @Date 2024/3/21
 **/
@Component
@Slf4j
@RefreshScope
public class MyScheduledTask {
    private final ImportService importService;

    @Autowired
    public MyScheduledTask(ImportService importService) {
        this.importService = importService;
    }

    /**
     * 每天凌晨3点执行 curl 命令
     * 同步 mysql 数据到 es
     * 同步案由统计数据
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void executeCurlCommand() {
        importService.syncData();
    }
}
