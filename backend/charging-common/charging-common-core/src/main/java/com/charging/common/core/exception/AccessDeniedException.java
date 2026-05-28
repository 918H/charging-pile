package com.charging.common.core.exception;

/**
 * 访问拒绝异常
 */
public class AccessDeniedException extends BaseException {
    
    private static final long serialVersionUID = 1L;
    
    public AccessDeniedException() {
        super(403, "访问被拒绝");
    }
    
    public AccessDeniedException(String message) {
        super(403, message);
    }
}
