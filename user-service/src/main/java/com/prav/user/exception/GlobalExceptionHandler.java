package com.prav.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.prav.common.exception.BaseGlobalExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<com.prav.common.dto.ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        com.prav.common.dto.ErrorResponse error = com.prav.common.dto.ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<com.prav.common.dto.ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        com.prav.common.dto.ErrorResponse error = com.prav.common.dto.ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UserOperationException.class)
    public ResponseEntity<com.prav.common.dto.ErrorResponse> handleUserOperation(UserOperationException ex) {
        com.prav.common.dto.ErrorResponse error = com.prav.common.dto.ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
