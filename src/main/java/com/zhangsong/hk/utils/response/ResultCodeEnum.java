package com.zhangsong.hk.utils.response;

import lombok.Getter;

/**
 * @ClassName: ResultCodeEnum
 * @Description: 状态码枚举类
 * @Author: 张松
 * @Date: 2025/12/4 22:12
 * @Version: 1.0
 */
@Getter
public enum ResultCodeEnum {
    // 成功状态码
    SUCCESS(200, "成功"),
    CUSTOM_SUCCESS(2000, "自定义成功"),

    // 通用错误状态码
    FAIL(4000, "操作失败"),
    SERVER_ERROR(500, "服务器内部错误"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不被允许"),
    TOO_MANY_REQUESTS(429, "请求过多"),
    UNKNOWN_REASON(9999, "未知错误"),

    // 业务错误
    ADD_USER_ERROR(1000, "用户注册错误"),
    UPDATE_USER_ERROR(1001, "用户更新错误"),
    USER_NOT_FOUND(1002, "用户不存在"),
    USERNAME_EXISTS(1003, "用户名已存在"),
    PHONE_EXISTS(1004, "手机号已注册"),
    LOGIN_FAILED(1005, "登录失败");

    private final Integer code;    // 状态码
    private final String msg;  // 状态信息

    ResultCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ResultCodeEnum{" +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}