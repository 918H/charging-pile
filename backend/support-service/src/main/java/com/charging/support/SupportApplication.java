package com.charging.support;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication @EnableDiscoveryClient @MapperScan("com.charging.support.mapper")
public class SupportApplication { public static void main(String[] args) { SpringApplication.run(SupportApplication.class, args); }}
