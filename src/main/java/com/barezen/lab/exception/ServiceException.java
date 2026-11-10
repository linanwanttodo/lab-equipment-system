package com.barezen.lab.exception;

/** 业务校验异常：携带可直接展示给用户的错误信息 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
