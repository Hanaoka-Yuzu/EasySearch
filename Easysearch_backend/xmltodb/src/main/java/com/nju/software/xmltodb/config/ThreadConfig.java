package com.nju.software.xmltodb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description 线程池配置
 * @Author wxy
 * @Date 2024/2/19
 **/
@Configuration
@EnableAsync
public class ThreadConfig {

    @Value("#{T(java.lang.Runtime).getRuntime().availableProcessors()}")
    private int availableProcessors;

    public int getComputeThreads() {
        return availableProcessors + 1;
    }

    @Bean(name = "computeTaskExecutor")
    public ThreadPoolTaskExecutor computeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(availableProcessors + 1);
        //配置最大线程数
        executor.setMaxPoolSize(availableProcessors + 1);
        //配置队列大小
        executor.setQueueCapacity(1024);
        //线程的名称前缀
        executor.setThreadNamePrefix("ComputeExecutor-");
        //线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        //等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }

    @Bean(name = "ioTaskExecutor")
    public ThreadPoolTaskExecutor IOExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //配置核心线程数
        executor.setCorePoolSize(availableProcessors*2);
        //配置最大线程数
        executor.setMaxPoolSize(availableProcessors*2);
        //配置队列大小
        executor.setQueueCapacity(1024);
        //线程的名称前缀
        executor.setThreadNamePrefix("IOExecutor-");
        //线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        //等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        //设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
