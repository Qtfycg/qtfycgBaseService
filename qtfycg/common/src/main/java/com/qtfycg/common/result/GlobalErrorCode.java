/*
 * Copyright (c) 2026 qtfycg All rights reserved
 */

package com.qtfycg.common.result;

import lombok.Getter;

/**
 * 跨模块使用的通用响应码。
 *
 * <p>每个响应码同时定义 HTTP 状态码、稳定的业务码和默认提示信息。</p>
 */
@Getter
public enum GlobalErrorCode implements ErrorCode {
    /** 操作成功。 */
    SUCCESS(200, "SUCCESS", "操作成功"),

    /** 资源创建成功。 */
    CREATED(201, "CREATED", "创建成功"),

    /** 请求参数或请求内容不符合接口要求。 */
    BAD_REQUEST(400, "BAD_REQUEST", "请求参数错误"),

    /** 请求参数未通过校验。 */
    VALIDATION_ERROR(400, "VALIDATION_ERROR", "参数校验失败"),

    /** 用户未登录或登录凭证已失效。 */
    UNAUTHORIZED(401, "UNAUTHORIZED", "未登录或登录已失效"),

    /** 当前用户没有访问资源所需的权限。 */
    FORBIDDEN(403, "FORBIDDEN", "无权限访问"),

    /** 请求的资源不存在。 */
    NOT_FOUND(404, "NOT_FOUND", "请求资源不存在"),

    /** 当前资源不支持所使用的 HTTP 请求方法。 */
    METHOD_NOT_ALLOWED(405, "METHOD_NOT_ALLOWED", "请求方法不支持"),

    /** 请求频率超过服务限制。 */
    TOO_MANY_REQUESTS(429, "TOO_MANY_REQUESTS", "请求过于频繁"),

    /** 服务发生未预期的内部错误。 */
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "系统内部错误"),

    /** 服务当前不可用。 */
    SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE", "服务暂不可用");

    /** 与响应码对应的 HTTP 状态码。 */
    private final int status;

    /** 供客户端稳定识别的业务码。 */
    private final String code;

    /** 未指定自定义消息时使用的默认提示信息。 */
    private final String message;

    /**
     * 创建通用响应码。
     *
     * @param status HTTP 状态码
     * @param code 业务码
     * @param message 默认提示信息
     */
    GlobalErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
