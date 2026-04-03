package com.demo.exception;

import com.demo.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 处理参数校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ")
        );
        String message = sb.toString();
        log.warn("参数校验失败: {}", message);
        return Result.fail(1004, "参数校验失败: " + message);
    }

    // 处理通用异常
    @ExceptionHandler(Exception.class)
    public Result handle(Exception e) {
        log.error("系统发生未预期异常", e);
        return Result.fail(5000, "系统繁忙，请稍后再试");
    }}
