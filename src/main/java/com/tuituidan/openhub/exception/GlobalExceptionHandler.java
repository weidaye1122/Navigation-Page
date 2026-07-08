package com.tuituidan.openhub.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalExceptionHandler.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2021/2/27
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * authenticationException.
     *
     * @param throwable 认证异常
     * @return String
     */
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public String authenticationException(AuthenticationException throwable) {
        log.warn("登录失败：{}", throwable.getMessage());
        return "用户名或密码错误";
    }

    /**
     * 业务参数异常.
     *
     * @param throwable 参数异常
     * @return String
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String badRequest(RuntimeException throwable) {
        return throwable.getMessage();
    }

    /**
     * 其他未被捕获的异常.
     *
     * @param throwable 所有的未被捕获或定义的异常
     * @return String
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public String exception(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        return "服务异常，请稍后重试";
    }
}
