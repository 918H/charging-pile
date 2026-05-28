package com.charging.common.core.exception;

/**
 * 业务异常类
 */
public class BusinessException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    public BusinessException(String message) {
        super(500, message);
    }
    
    public BusinessException(Integer code, String message) {
        super(code, message);
    }
    
    public BusinessException(String message, Object... args) {
        super(500, String.format(message, args));
    }
    
    public BusinessException(Integer code, String message, Object... args) {
        super(code, String.format(message, args));
    }
}
