package com.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {
    /**
     * 上传文件存储路径
     */
    private String basePath = "./uploads/";

    /**
     * 允许的文件类型
     */
    private String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "webp"};

    /**
     * 单个文件最大大小 (MB)
     */
    private Long maxFileSize = 5L;

    /**
     * 请求总文件最大大小 (MB)
     */
    private Long maxRequestSize = 10L;

    /**
     * 访问映射路径
     */
    private String accessPath = "/uploads/**";
}
