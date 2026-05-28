package com.charging.common.core.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.charging.common.core.response.R;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 配置类
 */
@Configuration
public class SentinelConfig {

    /**
     * 自定义 blocked 处理
     */
    @Bean
    public BlockExceptionHandler blockExceptionHandler() {
        return (HttpServletRequest request, HttpServletResponse response, BlockException e) -> {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            
            String msg;
            if (e instanceof FlowException) {
                msg = "请求过于频繁，请稍后再试";
            } else if (e instanceof DegradeException) {
                msg = "服务暂时不可用，请稍后再试";
            } else if (e instanceof ParamFlowException) {
                msg = "参数限流，请稍后再试";
            } else if (e instanceof SystemBlockException) {
                msg = "系统保护，请稍后再试";
            } else {
                msg = "请求被限制，请稍后再试";
            }
            
            R<Void> result = R.fail(429, msg);
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.getObjectMapper().writeValue(response.getOutputStream(), result);
        };
    }

    /**
     * URL 清洗配置
     */
    @Bean
    public UrlCleaner urlCleaner() {
        return url -> {
            if (url.contains("/api/")) {
                // 统一处理 RESTful 风格的 URL
                return url.replaceAll("/api/\\w+/\\w+/\\d+", "/api/{module}/{resource}/{id}");
            }
            return url;
        };
    }

    /**
     * Web MVC 配置
     */
    @Bean
    public SentinelWebMvcConfig sentinelWebMvcConfig() {
        SentinelWebMvcConfig config = new SentinelWebMvcConfig();
        config.setHttpMethodSpecify(true);
        config.setWebContextUnify(true);
        config.setBlockExceptionHandler(blockExceptionHandler());
        config.setUrlCleaner(urlCleaner());
        return config;
    }
}
