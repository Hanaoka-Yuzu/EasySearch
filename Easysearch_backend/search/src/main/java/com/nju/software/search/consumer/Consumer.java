package com.nju.software.search.consumer;

import com.nju.software.search.service.CauseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import static com.nju.software.common.utils.Constant.SYNC;

/**
 * @Description 消息队列消费者
 * @Author wxy
 * @Date 2024/4/6
 **/
@Component
@Slf4j
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group}", topic = "${rocketmq.topic.causeCount}")
public class Consumer implements RocketMQListener<String> {

    private final CauseService causeService;

    public Consumer(CauseService causeService) {
        this.causeService = causeService;
    }

    @Override
    public void onMessage(String message) {
        if (!SYNC.equals(message)) {
            return;
        }
        log.info("开始消费同步案由统计数据消息");
        causeService.syncCauseCountData();
    }
}
