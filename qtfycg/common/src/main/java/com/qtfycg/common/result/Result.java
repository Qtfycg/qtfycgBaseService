/*
 * Copyright (c) 2026 qtfycg All rights reserved
 */

package com.qtfycg.common.result;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

/**
 * HTTP 接口统一响应。
 *
 * @param <T> 响应数据类型
 */
@Getter
public final class Result<T> {

    /** 请求是否成功。 */
    private final boolean success;

    /** 与本次业务结果对应的 HTTP 状态码。 */
    private final int status;

    /** 供客户端稳定识别和处理的业务码。 */
    private final String code;

    /** 面向客户端的结果说明。 */
    private final String message;

    /** 成功响应携带的业务数据；无数据或失败时为 {@code null}。 */
    private final T data;

    /** 响应对象的创建时间。 */
    private final LocalDateTime timestamp;

    /** 用于关联客户端响应与服务端日志的链路标识。 */
    private final String traceId;

    /**
     * 创建统一响应对象。
     *
     * @param success 请求是否成功
     * @param status HTTP 状态码
     * @param code 业务码
     * @param message 结果说明
     * @param data 响应数据
     * @param timestamp 响应创建时间
     * @param traceId 链路标识
     */
    private Result(
            boolean success,
            int status,
            String code,
            String message,
            T data,
            LocalDateTime timestamp,
            String traceId) {
        this.success = success;
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }

    /**
     * 创建不携带业务数据的成功响应。
     *
     * @return HTTP 状态码为 200 的成功响应
     */
    public static Result<Void> ok() {
        return ok(null);
    }

    /**
     * 创建携带业务数据的成功响应。
     *
     * @param data 响应数据
     * @param <T> 响应数据类型
     * @return HTTP 状态码为 200 的成功响应
     */
    public static <T> Result<T> ok(T data) {
        return from(true, GlobalErrorCode.SUCCESS, data, GlobalErrorCode.SUCCESS.getMessage());
    }

    /**
     * 创建资源创建成功响应。
     *
     * @param data 已创建的资源或其标识
     * @param <T> 响应数据类型
     * @return HTTP 状态码为 201 的成功响应
     */
    public static <T> Result<T> created(T data) {
        return from(true, GlobalErrorCode.CREATED, data, GlobalErrorCode.CREATED.getMessage());
    }

    /**
     * 使用错误码的默认提示信息创建失败响应。
     *
     * @param errorCode 错误码，不可为 {@code null}
     * @param <T> 响应数据类型
     * @return 不携带业务数据的失败响应
     * @throws NullPointerException 当错误码为 {@code null} 时抛出
     */
    public static <T> Result<T> fail(ErrorCode errorCode) {
        ErrorCode requiredErrorCode = Objects.requireNonNull(errorCode, "errorCode 不能为空");
        return fail(requiredErrorCode, requiredErrorCode.getMessage());
    }

    /**
     * 使用自定义提示信息创建失败响应。
     *
     * @param errorCode 错误码，不可为 {@code null}
     * @param message 返回给客户端的提示信息，不可为 {@code null}
     * @param <T> 响应数据类型
     * @return 不携带业务数据的失败响应
     * @throws NullPointerException 当错误码或提示信息为 {@code null} 时抛出
     */
    public static <T> Result<T> fail(ErrorCode errorCode, String message) {
        ErrorCode requiredErrorCode = Objects.requireNonNull(errorCode, "errorCode 不能为空");
        String requiredMessage = Objects.requireNonNull(message, "message 不能为空");
        return from(false, requiredErrorCode, null, requiredMessage);
    }

    /**
     * 返回附带链路标识的新响应，保持响应对象不可变。
     *
     * @param traceId 链路标识
     * @return 属性相同且附带指定链路标识的新响应
     */
    public Result<T> withTraceId(String traceId) {
        return new Result<>(success, status, code, message, data, timestamp, traceId);
    }

    /**
     * 根据错误码契约创建响应并记录当前时间。
     *
     * @param success 请求是否成功
     * @param errorCode 提供状态码和业务码的错误码定义
     * @param data 响应数据
     * @param message 结果说明
     * @param <T> 响应数据类型
     * @return 新创建的统一响应
     */
    private static <T> Result<T> from(
            boolean success, ErrorCode errorCode, T data, String message) {
        return new Result<>(
                success,
                errorCode.getStatus(),
                errorCode.getCode(),
                message,
                data,
                LocalDateTime.now(),
                null);
    }
}
