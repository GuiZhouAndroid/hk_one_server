package com.zhangsong.hk.utils.exception;

import com.zhangsong.hk.utils.response.ResultCodeEnum;

/**
 * @ClassName: ValidationException
 * @Description: 验证异常
 * @Author: 张松
 * @Date: 2025/12/4 22:58
 * @Version: 1.0
 */
public class ValidationException extends CustomException {
    public ValidationException(String message) {
        super(ResultCodeEnum.BAD_REQUEST.getCode(), message);
    }

    public ValidationException(String message, Throwable cause) {
        super(ResultCodeEnum.BAD_REQUEST.getCode(), message, cause);
    }
}