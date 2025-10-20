package com.dnrush.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("test") // 只在 test profile 下啟用
public class TestSecurityConfig {
    
    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // 允許所有請求，不需要認證
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf.disable()) // 禁用 CSRF 以簡化測試
            .headers(headers -> headers.frameOptions().disable()); // 允許 H2 console (如果需要)
            
        return http.build();
    }
}