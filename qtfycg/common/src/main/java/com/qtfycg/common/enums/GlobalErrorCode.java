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

    /*创建成功*/
    CREATED(201, "CREATED", "success"),
    ;

    private final Integer status;

    private final String code;

    private final String message;

}
