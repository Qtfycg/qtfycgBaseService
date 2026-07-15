/*
 * Copyright (c)
 * 2026
 * qtfycg
 * All rights reserved
 */

/*
 * Copyright (c)
 */

package com.qtfycg.framework.exception;

import com.qtfycg.common.enums.GlobalErrorCode;
import com.qtfycg.common.exception.BusinessException;
import com.qtfycg.common.result.ErrorCode;
import com.qtfycg.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TRACE_ID_KEY = "traceId";

    /**
     * 业务异常。
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException exception) {
        log.warn("Business exception: {}", exception.getMessage());

        return buildResponse(exception.getErrorCode(), exception.getMessage());
    }

    /**
     * 请求体参数校验异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildResponse(GlobalErrorCode.PARAM_ERROR, normalizeMessage(message, GlobalErrorCode.PARAM_ERROR));
    }

    /**
     * 表单绑定参数校验异常。
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Void>> handleBindException(BindException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildResponse(GlobalErrorCode.PARAM_ERROR, normalizeMessage(message, GlobalErrorCode.PARAM_ERROR));
    }

    /**
     * 方法参数校验异常。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolationException(ConstraintViolationException exception) {
        return buildResponse(GlobalErrorCode.PARAM_ERROR, normalizeMessage(exception.getMessage(), GlobalErrorCode.PARAM_ERROR));
    }

    /**
     * 缺少必填请求参数。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        String message = "Missing required parameter: " + exception.getParameterName();
        return buildResponse(GlobalErrorCode.PARAM_ERROR, message);
    }

    /**
     * 请求参数类型不匹配。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = "Parameter type mismatch: " + exception.getName();
        return buildResponse(GlobalErrorCode.PARAM_ERROR, message);
    }

    /**
     * 请求体无法读取，例如 JSON 格式错误。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return buildResponse(GlobalErrorCode.PARAM_ERROR, "Request body is invalid");
    }

    /**
     * HTTP 方法不支持。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return buildResponse(GlobalErrorCode.METHOD_NOT_ALLOWED, GlobalErrorCode.METHOD_NOT_ALLOWED.getMessage());
    }

    /**
     * Content-Type 不支持。
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        return buildResponse(GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE, GlobalErrorCode.UNSUPPORTED_MEDIA_TYPE.getMessage());
    }

    /**
     * 资源不存在。
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Void>> handleNoHandlerFoundException(NoHandlerFoundException exception) {
        return buildResponse(GlobalErrorCode.NOT_FOUND, GlobalErrorCode.NOT_FOUND.getMessage());
    }

    /**
     * 未认证。
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<Void>> handleAuthenticationException(AuthenticationException exception) {
        return buildResponse(GlobalErrorCode.UNAUTHORIZED, GlobalErrorCode.UNAUTHORIZED.getMessage());
    }

    /**
     * 无权限。
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDeniedException(AccessDeniedException exception) {
        return buildResponse(GlobalErrorCode.FORBIDDEN, GlobalErrorCode.FORBIDDEN.getMessage());
    }

    /**
     * 系统兜底异常。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception exception) {
        log.error("Unexpected exception", exception);

        return buildResponse(GlobalErrorCode.SYSTEM_ERROR, GlobalErrorCode.SYSTEM_ERROR.getMessage());
    }

    private ResponseEntity<Result<Void>> buildResponse(ErrorCode errorCode, String message) {
        Result<Void> result = Result.<Void>fail(errorCode, message).withTraceId(MDC.get(TRACE_ID_KEY));

        return ResponseEntity
                .status(HttpStatusCode.valueOf(errorCode.getStatus()))
                .body(result);
    }

    private String normalizeMessage(String message, ErrorCode fallbackErrorCode) {
        if (message == null || message.isBlank()) {
            return fallbackErrorCode.getMessage();
        }
        return message;
    }
}
