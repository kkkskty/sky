package com.sky.exception;

/**
 * 业务异常
 */
//全局异常处理器
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
