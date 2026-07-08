package com.qtfycg.common.exception;

import com.qtfycg.common.enums.GlobalErrorCode;
import com.qtfycg.common.result.ErrorCode;
import lombok.Getter;

import java.io.Serial;
import java.util.Objects;

/**
 * 业务异常。
 * <p>
 * 用于业务规则校验失败、资源冲突、状态不允许流转等可预期异常。
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码定义。
     */
    private final ErrorCode errorCode;

    /**
     * 使用默认业务异常码。
     *
     * @param message 异常信息
     */
    public BusinessException(String message) {
        this(GlobalErrorCode.BUSINESS_ERROR, message);
    }

    /**
     * 使用指定错误码，异常信息使用错误码默认信息。
     *
     * @param errorCode 错误码定义
     */
    public BusinessException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    /**
     * 使用指定错误码，并覆盖默认异常信息。
     *
     * @param errorCode 错误码定义
     * @param message   异常信息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(resolveMessage(errorCode, message));
        this.errorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
    }

    private static String resolveMessage(ErrorCode errorCode, String message) {
        ErrorCode resolvedErrorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
        return message == null ? resolvedErrorCode.getMessage() : message;
    }

    public Integer getStatus() {
        return errorCode.getStatus();
    }

    public String getCode() {
        return errorCode.getCode();
    }
}