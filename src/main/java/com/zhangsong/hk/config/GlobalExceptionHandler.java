package com.zhangsong.hk.config;

import com.zhangsong.hk.utils.exception.CustomException;
import com.zhangsong.hk.utils.exception.ExceptionUtils;
import com.zhangsong.hk.utils.response.R;
import com.zhangsong.hk.utils.response.ResultCodeEnum;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * @ClassName: GlobalExceptionHandler
 * @Description: 全局异常处理器
 * @Author: 张松
 * @Date: 2025/12/4 23:01
 * @Version: 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(CustomException.class)
    public R<Void> handleCustomException(CustomException e) {
        log.warn("业务异常: {} - {}", e.getCode(), e.getMessage());
        return R.business(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常（@RequestBody @Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数验证失败: {}", message);
        return R.fail(ResultCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理参数绑定异常（@RequestParam）
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数绑定失败: {}", message);
        return R.fail(ResultCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        log.warn("约束违反: {}", message);
        return R.fail(ResultCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = String.format("缺少必要参数: %s", e.getParameterName());
        log.warn(message);
        return R.fail(ResultCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("参数类型错误: %s 应为 %s 类型",
                e.getName(), e.getRequiredType().getSimpleName());
        log.warn(message);
        return R.fail(ResultCodeEnum.BAD_REQUEST.getCode(), message);
    }

    /**
     * 处理HTTP方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = String.format("不支持 %s 请求方法", e.getMethod());
        log.warn(message);
        return R.fail(ResultCodeEnum.METHOD_NOT_ALLOWED.getCode(), message);
    }

    /**
     * 处理数据库异常
     */
    @ExceptionHandler(DataAccessException.class)
    public R<Void> handleDataAccessException(DataAccessException e) {
        log.error("数据库操作异常: {}", e.getMessage(), e);
        // 生产环境可以返回更友好的提示
        return R.error("数据库操作失败，请稍后重试");
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        // 使用 ExceptionUtils 获取堆栈信息
        String stackTrace = ExceptionUtils.getStackTrace(e);

        // 记录完整错误日志
        log.error("系统异常: {}", stackTrace);

        // 生产环境返回友好提示，开发环境可以返回详细错误
        String responseMessage = isProduction() ?
                "系统繁忙，请稍后重试" :
                e.getMessage();

        return R.error(responseMessage);
    }

    /**
     * 判断是否为生产环境
     */
    private boolean isProduction() {
        String env = System.getProperty("spring.profiles.active", "dev");
        return "prod".equals(env) || "production".equals(env);
    }
}