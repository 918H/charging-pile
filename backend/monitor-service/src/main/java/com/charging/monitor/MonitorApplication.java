package com.charging.monitor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication @EnableDiscoveryClient @MapperScan("com.charging.monitor.mapper")
public class MonitorApplication { public static void main(String[] args) { SpringApplication.run(MonitorApplication.class, args); }}
