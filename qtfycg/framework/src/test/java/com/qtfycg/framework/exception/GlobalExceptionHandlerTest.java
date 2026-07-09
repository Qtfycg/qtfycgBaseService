package com.qtfycg.framework.exception;

import com.qtfycg.common.enums.GlobalErrorCode;
import com.qtfycg.common.exception.BusinessException;
import com.qtfycg.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void businessExceptionUsesBusinessErrorCodeAndTraceId() {
        MDC.put("traceId", "trace-1");

        ResponseEntity<Result<Void>> response = handler.handleBusinessException(new BusinessException("库存不足"));

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        Result<Void> body = requireBody(response);
        assertThat(body.getCode()).isEqualTo(GlobalErrorCode.BUSINESS_ERROR.getCode());
        assertThat(body.getMessage()).isEqualTo("库存不足");
        assertThat(body.getTraceId()).isEqualTo("trace-1");
    }

    @Test
    void missingParameterReturnsParameterError() {
        ResponseEntity<Result<Void>> response = handler.handleMissingServletRequestParameterException(
                new MissingServletRequestParameterException("id", "Long")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(requireBody(response).getCode()).isEqualTo(GlobalErrorCode.PARAM_ERROR.getCode());
    }

    @Test
    void constraintViolationReturnsParameterError() {
        ResponseEntity<Result<Void>> response = handler.handleConstraintViolationException(
                new ConstraintViolationException("id must not be null", null)
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(requireBody(response).getCode()).isEqualTo(GlobalErrorCode.PARAM_ERROR.getCode());
    }

    @Test
    void unreadableMessageReturnsParameterError() {
        ResponseEntity<Result<Void>> response = handler.handleHttpMessageNotReadableException(
                new HttpMessageNotReadableException("JSON parse error", null)
        );

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(requireBody(response).getCode()).isEqualTo(GlobalErrorCode.PARAM_ERROR.getCode());
    }

    @Test
    void unsupportedMethodReturnsMethodNotAllowed() {
        ResponseEntity<Result<Void>> response = handler.handleHttpRequestMethodNotSupportedException(
                new HttpRequestMethodNotSupportedException("TRACE")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(405);
        assertThat(requireBody(response).getCode()).isEqualTo(GlobalErrorCode.METHOD_NOT_ALLOWED.getCode());
    }

    @Test
    void unsupportedMediaTypeReturnsUnsupportedMediaType() {
        ResponseEntity<Result<Void>> response = handler.handleHttpMediaTypeNotSupportedException(
                new HttpMediaTypeNotSupportedException("application/xml")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(415);
        assertThat(requireBody(response).getCode()).isEqualTo(GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE.getCode());
    }

    @Test
    void authenticationExceptionReturnsUnauthorized() {
        ResponseEntity<Result<Void>> response = handler.handleAuthenticationException(
                new BadCredentialsException("bad credentials")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(401);
        assertThat(requireBody(response).getCode()).isEqualTo(GlobalErrorCode.UNAUTHORIZED.getCode());
    }

    @Test
    void accessDeniedExceptionReturnsForbidden() {
        ResponseEntity<Result<Void>> response = handler.handleAccessDeniedException(
                new AccessDeniedException("denied")
        );

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(requireBody(response).getCode()).isEqualTo(GlobalErrorCode.FORBIDDEN.getCode());
    }

    @Test
    void unexpectedExceptionReturnsSystemError() {
        ResponseEntity<Result<Void>> response = handler.handleException(new IllegalStateException("boom"));

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(requireBody(response).getCode()).isEqualTo(GlobalErrorCode.SYSTEM_ERROR.getCode());
        assertThat(requireBody(response).getMessage()).isEqualTo(GlobalErrorCode.SYSTEM_ERROR.getMessage());
    }

    private static Result<Void> requireBody(ResponseEntity<Result<Void>> response) {
        return Objects.requireNonNull(response.getBody());
    }
}
