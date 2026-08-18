/*
 * Copyright (c) 2026 qtfycg All rights reserved
 */

package com.qtfycg.common.result;

/**
 * 统一错误码契约。
 */
public interface ErrorCode {

    /**
     * 获取与当前业务结果对应的 HTTP 状态码。
     *
     * @return HTTP 状态码
     */
    int getStatus();

    /**
     * 获取供客户端稳定识别的业务码。
     *
     * @return 业务码
     */
    String getCode();

    /**
     * 获取默认的用户提示信息。
     *
     * @return 默认提示信息
     */
    String getMessage();
}
