package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 项目启动项
 */
@SpringBootApplication
@ComponentScan({"com.example.enc.base", "com.example.demo"})
public class StartApplication  {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

}
