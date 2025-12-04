package com.zhangsong.hk.utils.exception;

import com.zhangsong.hk.utils.response.ResultCodeEnum;
import lombok.Getter;

/**
 * @ClassName: CustomException
 * @Description: 自定义业务异常类
 * @Author: 张松
 * @Date: 2025/12/4 22:57
 * @Version: 1.0
 */
@Getter
public class CustomException extends RuntimeException {
    private final Integer code;
    private final String detailMessage;

    /**
     * 使用枚举构造异常
     */
    public CustomException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMsg());
        this.code = resultCodeEnum.getCode();
        this.detailMessage = resultCodeEnum.getMsg();
    }

    /**
     * 使用枚举和自定义消息构造异常
     */
    public CustomException(ResultCodeEnum resultCodeEnum, String detailMessage) {
        super(resultCodeEnum.getMsg());
        this.code = resultCodeEnum.getCode();
        this.detailMessage = detailMessage;
    }

    /**
     * 使用自定义状态码和消息构造异常
     */
    public CustomException(Integer code, String message) {
        super(message);
        this.code = code;
        this.detailMessage = message;
    }

    /**
     * 使用自定义状态码、消息和原因构造异常
     */
    public CustomException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.detailMessage = message;
    }
}