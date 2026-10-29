package com.barezen.exception;

/** 业务异常：携带 HTTP 状态码与提示消息。 */
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}
