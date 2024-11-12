package com.nju.software.search.advice;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.nju.software.common.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

/**
 * Guava cache 适用于读多写少的情况
 */
@Component
@Aspect
@Slf4j
@EnableScheduling
public class RequestCacheAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final LoadingCache<String, Result> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(500)                   // 默认本地 500 条缓存上限
            .recordStats()
            .build(new CacheLoader<>() {
                @Override
                public Result load(String key) { // 本地缓存未命中
                    return null;
                }
            });


    @Pointcut("execution(* com.nju.software.search.controller.SearchController.*(..))")
    public void searchControllerMethods() {
    }

    @Around("searchControllerMethods()")
    public Object cacheRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        String cacheKey = buildCacheKey(joinPoint);

        //1. 检查本地缓存
        Result cachedResult = cache.getIfPresent(cacheKey);
        if (cachedResult != null) {
//            log.info("Cache hit for key: {}", cacheKey);
            return cachedResult;
        }

        //2. 检查 redis 缓存
        String s = redisTemplate.opsForValue().get(cacheKey);
        if (s != null) {
            cachedResult = JSON.parseObject(s, Result.class);
            cache.put(cacheKey, cachedResult);
            return cachedResult;
        }

//        log.info("Cache miss for key: {}", cacheKey);
        Result result = (Result) joinPoint.proceed();
        cache.put(cacheKey, result);
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(result), 10, TimeUnit.MINUTES);
        return result;
    }

    private String buildCacheKey(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String path = request.getRequestURI();

        Object[] args = joinPoint.getArgs();

        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(path);
        for (Object arg : args) {
            keyBuilder.append(arg.toString());
        }
        return keyBuilder.toString();
    }

    /**
     * 定时任务，观察命中情况，暂定为一小时观测一次
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void logCacheStatistics() {
        CacheStats stats = cache.stats();
        log.info("Cache statistics - hit rate: {}, miss rate: {}", stats.hitRate(), stats.missRate());
        log.info("Cache statistics - hits: {}, misses: {}", stats.hitCount(), stats.missCount());
    }
}
