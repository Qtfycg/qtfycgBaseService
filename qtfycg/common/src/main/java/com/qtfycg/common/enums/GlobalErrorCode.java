package com.qtfycg.common.enums;

import com.qtfycg.common.result.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {


    /*成功*/
    OK(200, "OK", "success"),

    /*系统异常*/
    FAIL(500, "FAIL", "fail"),

    /*业务异常*/
    BUSINESS_ERROR(400, "BUSINESS_ERROR", "business error"),

    /*参数异常*/
    PARAM_ERROR(400, "PARAM_ERROR", "parameter error"),

    /*未认证*/
    UNAUTHORIZED(401, "UNAUTHORIZED", "unauthorized"),

    /*无权限*/
    FORBIDDEN(403, "FORBIDDEN", "forbidden"),

    /*资源不存在*/
    NOT_FOUND(404, "NOT_FOUND", "not found"),

    /*请求方法不支持*/
    METHOD_NOT_ALLOWED(405, "METHOD_NOT_ALLOWED", "method not allowed"),

    /*请求媒体类型不支持*/
    UNSUPPORTED_MEDIA_TYPE(415, "UNSUPPORTED_MEDIA_TYPE", "unsupported media type"),

    /*系统异常*/
    SYSTEM_ERROR(500, "SYSTEM_ERROR", "system error"),

    /*创建成功*/
    CREATED(201, "CREATED", "success"),
    ;

    private final Integer status;

    private final String code;

    private final String message;

}
