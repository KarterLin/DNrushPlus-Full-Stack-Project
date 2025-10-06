package com.dnrush.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 靜態資源處理
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // 上傳檔案處理
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        
        // 前端資源處理
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
    }
}
