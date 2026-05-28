package com.charging.common.core.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 * 
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 成功响应（无数据）
     */
    public static <T> R<T> ok() {
        return restResult(200, "success", null);
    }
    
    /**
     * 成功响应（有数据）
     */
    public static <T> R<T> ok(T data) {
        return restResult(200, "success", data);
    }
    
    /**
     * 成功响应（自定义消息）
     */
    public static <T> R<T> ok(String message, T data) {
        return restResult(200, message, data);
    }
    
    /**
     * 失败响应
     */
    public static <T> R<T> fail(String message) {
        return restResult(500, message, null);
    }
    
    /**
     * 失败响应（自定义状态码）
     */
    public static <T> R<T> fail(Integer code, String message) {
        return restResult(code, message, null);
    }
    
    /**
     * 失败响应（自定义状态码和数据）
     */
    public static <T> R<T> fail(Integer code, String message, T data) {
        return restResult(code, message, data);
    }
    
    /**
     * 通用响应构建方法
     */
    public static <T> R<T> restResult(Integer code, String message, T data) {
        R<T> result = new R<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    /**
     * 判断是否成功
     */
    public static <T> Boolean isSuccess(R<T> result) {
        return result != null && result.getCode() != null && result.getCode() == 200;
    }
}
