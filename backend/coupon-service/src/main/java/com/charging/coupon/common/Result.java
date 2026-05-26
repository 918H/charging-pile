package com.charging.coupon.common;

import lombok.Data;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer code;
    private String msg;
    private T data;
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
