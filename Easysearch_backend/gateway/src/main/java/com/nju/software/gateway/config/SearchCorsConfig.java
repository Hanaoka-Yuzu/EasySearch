//package com.nju.software.gateway.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//
///**
// * SearchCorsConfig
// *
// * @create 2024-03-21 10:16
// * 跨域配置，主要方便前端访问
// */
//@Configuration
//public class SearchCorsConfig {
//    @Bean
//    public CorsWebFilter corsWebFilter(){
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//
//        // 配置跨域
//        config.addAllowedHeader("*");
//        config.addAllowedOrigin("*");
//        config.addAllowedMethod("*");
//        config.setAllowCredentials(true);
//        source.registerCorsConfiguration("/**",config);
//
//        return new CorsWebFilter(source);
//    }
//}
