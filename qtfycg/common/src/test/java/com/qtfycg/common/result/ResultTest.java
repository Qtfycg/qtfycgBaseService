/*
 * Copyright (c) 2026 qtfycg All rights reserved
 */

package com.qtfycg.common.result;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    void shouldCreateSuccessResponseWithData() {
        Result<String> response = Result.ok("data");

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getCode()).isEqualTo("SUCCESS");
        assertThat(response.getMessage()).isEqualTo("操作成功");
        assertThat(response.getData()).isEqualTo("data");
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getTraceId()).isNull();
    }

    @Test
    void shouldCreateEmptySuccessResponse() {
        Result<Void> response = Result.ok();

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isNull();
    }

    @Test
    void shouldCreateCreatedResponse() {
        Result<Long> response = Result.created(1L);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getStatus()).isEqualTo(201);
        assertThat(response.getCode()).isEqualTo("CREATED");
        assertThat(response.getMessage()).isEqualTo("创建成功");
        assertThat(response.getData()).isEqualTo(1L);
    }

    @Test
    void shouldCreateFailureResponseFromErrorCode() {
        Result<Object> response = Result.fail(GlobalErrorCode.NOT_FOUND);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getCode()).isEqualTo("NOT_FOUND");
        assertThat(response.getMessage()).isEqualTo("请求资源不存在");
        assertThat(response.getData()).isNull();
    }

    @Test
    void shouldAllowFailureMessageOverride() {
        Result<Void> response = Result.fail(GlobalErrorCode.BAD_REQUEST, "用户名不能为空");

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getCode()).isEqualTo("BAD_REQUEST");
        assertThat(response.getMessage()).isEqualTo("用户名不能为空");
    }

    @Test
    void shouldReturnNewResponseWithTraceId() {
        Result<String> original = Result.ok("data");
        Result<String> traced = original.withTraceId("trace-123");

        assertThat(traced).isNotSameAs(original);
        assertThat(original.getTraceId()).isNull();
        assertThat(traced.getTraceId()).isEqualTo("trace-123");
        assertThat(traced.getTimestamp()).isEqualTo(original.getTimestamp());
    }

    @Test
    void shouldRejectMissingErrorCodeAndMessage() {
        assertThatNullPointerException()
                .isThrownBy(() -> Result.fail(null))
                .withMessage("errorCode 不能为空");
        assertThatNullPointerException()
                .isThrownBy(() -> Result.fail(GlobalErrorCode.BAD_REQUEST, null))
                .withMessage("message 不能为空");
    }
}
