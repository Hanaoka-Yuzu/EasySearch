package com.nju.software.download.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @Description 消费者
 * @Author wxy
 * @Date 2024/4/3
 **/
@Component
@Slf4j
@RefreshScope
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group}",
        topic = "${rocketmq.topic.saveDoc}",
        maxReconsumeTimes = 3,
        messageModel = MessageModel.CLUSTERING)
public class Consumer1 implements RocketMQListener<String> {
    private final Consumer consumer;

    @Autowired
    public Consumer1(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onMessage(String message) {
        consumer.onMessage(message);
    }
}
