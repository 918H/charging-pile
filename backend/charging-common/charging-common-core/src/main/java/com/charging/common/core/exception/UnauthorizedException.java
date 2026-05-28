package com.charging.common.core.exception;

/**
 * 未授权异常
 */
public class UnauthorizedException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    public UnauthorizedException() {
        super(401, "未授权访问");
    }
    
    public UnauthorizedException(String message) {
        super(401, message);
    }
}
