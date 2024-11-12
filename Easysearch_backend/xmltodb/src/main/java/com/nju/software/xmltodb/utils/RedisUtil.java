package com.nju.software.xmltodb.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Description redis工具类
 * @Author wxy
 * @Date 2024/4/22
 **/
@Slf4j
@Service
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取自增的序列号
     *
     * @param key redis主键
     * @return 序列号
     */
    public String getIncNum(String key) {
        String result;
        if (key == null) {
            key = "default";
        }
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().set(key, 0);
        }
        Long value = redisTemplate.opsForValue().increment(key);
        result = String.valueOf(value);
        return result;
    }
}