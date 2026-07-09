package com.qtfycg.common.result;

import com.qtfycg.common.enums.GlobalErrorCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final boolean success;

    /**
     * HTTP 状态码
     */
    private final Integer status;

    /**
     * 业务状态码
     */
    private final String code;

    /**
     * 提示信息
     */
    private final String message;

    /**
     * 响应数据
     */
    private final T data;
    /**
     * 响应时间
     */
    private final LocalDateTime timestamp;
    /**
     * 链路追踪Id
     */
    private String traceId;

    /**
     * 创建通用响应对象。
     *
     * @param success   是否成功
     * @param errorCode 响应码定义
     * @param message   提示信息
     * @param data      响应数据
     */
    private Result(boolean success, ErrorCode errorCode, String message, T data) {
        ErrorCode resolvedErrorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
        this.success = success;
        this.status = resolvedErrorCode.getStatus();
        this.code = resolvedErrorCode.getCode();
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 创建无响应数据的成功结果。
     *
     * @param <T> 响应数据类型
     * @return 成功响应
     */
    public static <T> Result<T> ok() {
        return ok(null);
    }

    /**
     * 创建带响应数据的成功结果。
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功响应
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(true, GlobalErrorCode.OK, GlobalErrorCode.OK.getMessage(), data);
    }

    /**
     * 根据错误码创建失败结果，提示信息使用错误码默认信息。
     *
     * @param errorCode 错误码定义
     * @param <T>       响应数据类型
     * @return 失败响应
     */
    public static <T> Result<T> fail(ErrorCode errorCode) {
        ErrorCode resolvedErrorCode = Objects.requireNonNull(errorCode, "errorCode must not be null");
        return fail(resolvedErrorCode, resolvedErrorCode.getMessage());
    }

    /**
     * 根据错误码创建失败结果，并覆盖默认提示信息。
     *
     * @param errorCode 错误码定义
     * @param message   自定义提示信息
     * @param <T>       响应数据类型
     * @return 失败响应
     */
    public static <T> Result<T> fail(ErrorCode errorCode, String message) {
        return new Result<>(false, errorCode, message, null);
    }

    /**
     * 创建成功，HTTP 状态码为 201。
     *
     */
    public static <T> Result<T> created(T data) {
        return new Result<>(true, GlobalErrorCode.CREATED, GlobalErrorCode.CREATED.getMessage(), data);
    }

    /**
     * 设置链路追踪 ID。
     *
     * @param traceId 链路追踪 ID
     * @return 当前响应对象
     */
    public Result<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}
