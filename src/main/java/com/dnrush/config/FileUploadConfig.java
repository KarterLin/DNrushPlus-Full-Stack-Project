package com.dnrush.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // 設置無限制檔案大小
        factory.setMaxFileSize(DataSize.ofBytes(-1));
        factory.setMaxRequestSize(DataSize.ofBytes(-1));
        
        return factory.createMultipartConfig();
    }
}