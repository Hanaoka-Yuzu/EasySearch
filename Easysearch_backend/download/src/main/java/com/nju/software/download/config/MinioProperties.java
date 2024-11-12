package com.nju.software.download.config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Minio参数配置类
 *
 * @author Suzcer
 */
@Data
@Configuration
public class MinioProperties {

    /**
     * 对象存储名称
     */
    @Value("${oss.name}")
    private String name;

    /**
     * 对象存储服务的URL
     */
    @Value("${oss.endpoint}")
    private String endpoint;

    /**
     * Access key 账户ID，默认为 minioadmin
     */
    @Value("${oss.accessKey}")
    private String accessKey;

    /**
     * Secret key 密码，默认为 minioadmin
     */
    @Value("${oss.secretKey}")
    private String secretKey;

    /**
     * 默认的存储桶名称
     */
    private String bucketName = "elastic";

    /**
     * 可上传的文件后缀名
     */
    @Value("${oss.fileExt}")
    private String[] fileExt;

}