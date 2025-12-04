package com.zhangsong.hk.utils.exception;

import com.zhangsong.hk.utils.response.ResultCodeEnum;

/**
 * @ClassName: BusinessException
 * @Description: 业务异常
 * @Author: 张松
 * @Date: 2025/12/4 22:59
 * @Version: 1.0
 */
public class BusinessException extends CustomException {
    public BusinessException(String message) {
        super(ResultCodeEnum.ADD_USER_ERROR.getCode(), message);
    }
}