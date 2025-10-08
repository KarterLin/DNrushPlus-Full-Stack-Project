package com.dnrush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DnRushPlusWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(DnRushPlusWebsiteApplication.class, args);
    }

}
// karter note:
// 如果8080被占用:
// netstat -ano | findstr :8080   
// 找到pid後:
// tasklist | findstr <pid>     <刪除PID
// taskkill /F /PID <pid>        <刪除先前運行的spring boot 應用