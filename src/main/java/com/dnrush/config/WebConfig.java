package com.dnrush.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
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
