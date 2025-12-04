package com.zhangsong.hk.utils.exception;

import com.zhangsong.hk.utils.response.ResultCodeEnum;

/**
 * @ClassName: AuthenticationException
 * @Description: 认证异常
 * @Author: 张松
 * @Date: 2025/12/4 22:59
 * @Version: 1.0
 */
public class AuthenticationException extends CustomException {
    public AuthenticationException(String message) {
        super(ResultCodeEnum.UNAUTHORIZED.getCode(), message);
    }
}