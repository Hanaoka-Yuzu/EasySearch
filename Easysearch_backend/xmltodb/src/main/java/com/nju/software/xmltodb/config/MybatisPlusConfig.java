package com.nju.software.xmltodb.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description MybatisPlus配置
 * @Author wxy
 * @Date 2024/3/4
 **/
@Configuration
@MapperScan("com.nju.software.xmltodb.mapper")
public class MybatisPlusConfig {
    @Bean
    public MySqlInjector insertBatchSqlInjector() {
        return new MySqlInjector();
    }
}