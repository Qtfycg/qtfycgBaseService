package com.qtfycg.framework.exception;

import com.qtfycg.common.exception.BusinessException;
import com.qtfycg.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 业务异常。
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException exception) {
        Result<Void> result = Result
                .fail(exception.getErrorCode(), exception.getMessage());

        return ResponseEntity
                .status(HttpStatusCode.valueOf(exception.getStatus()))
                .body(result);
    }
}
