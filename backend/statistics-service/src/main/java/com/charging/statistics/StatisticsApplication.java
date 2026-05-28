package com.charging.statistics;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.charging.statistics.mapper")
public class StatisticsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(StatisticsApplication.class, args);
    }
}
