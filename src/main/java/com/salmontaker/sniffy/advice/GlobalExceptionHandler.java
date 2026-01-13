package com.salmontaker.sniffy.advice;

import com.salmontaker.sniffy.common.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private String getRequestDetails(HttpServletRequest request) {
        return String.format("[%s] %s (IP: %s, User-Agent: %s)",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException e,
                                                              HttpServletRequest request) {
        log.warn("{} - {}", getRequestDetails(request), e.getMessage());
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e,
                                                                     HttpServletRequest request) {
        log.warn("{} - {}", getRequestDetails(request), e.getMessage());
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                     HttpServletRequest request) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .toList();

        log.warn("{} - {}", getRequestDetails(request), e.getMessage());
        return ApiResponse.error(String.join(", ", errors));
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<String> handleBadCredentialsException(BadCredentialsException e,
                                                             HttpServletRequest request) {
        log.warn("{} - {}", getRequestDetails(request), e.getMessage());
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<String> handleUnauthenticated(AuthenticationCredentialsNotFoundException e) {
        // 로그인 여부 체크용이므로 로깅 제외
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<String> handleAccessDeniedException(AccessDeniedException e,
                                                           HttpServletRequest request) {
        log.warn("{} - {}", getRequestDetails(request), e.getMessage());
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<String> handleNoSuchElementException(NoSuchElementException e,
                                                            HttpServletRequest request) {
        log.warn("{} - {}", getRequestDetails(request), e.getMessage());
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<String> handleNoResourceFoundException(NoResourceFoundException e,
                                                              HttpServletRequest request) {
        log.warn("{} - {}", getRequestDetails(request), e.getMessage());
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResponse<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e,
                                                                            HttpServletRequest request) {
        log.warn("{} - {}", getRequestDetails(request), e.getMessage());
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<String> handleIllegalStateException(IllegalStateException e,
                                                           HttpServletRequest request) {
        log.warn("{} - {}", getRequestDetails(request), e.getMessage());
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleException(Exception e,
                                               HttpServletRequest request) {
        log.error("{} - {}", getRequestDetails(request), e.getMessage(), e);
        return ApiResponse.error(e.getMessage());
    }
}
