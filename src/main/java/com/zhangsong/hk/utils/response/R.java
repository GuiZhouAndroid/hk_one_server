package com.zhangsong.hk.utils.response;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: R
 * @Description: * 统一返回格式类，使用泛型 T 来支持不同类型的返回数据
 * @Author: 张松
 * @Date: 2025/12/4 22:13
 * @Version: 1.0
 */
@SuppressWarnings("unchecked")
@Data
public class R<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回的消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 无参构造器
     */
    private R() {
    }

    /**
     * 设置返回消息
     *
     * @param msg 消息内容
     * @return 当前对象
     */
    public R<T> msg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * 设置状态码
     *
     * @param code 状态码
     * @return 当前对象
     */
    public R<T> code(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * 返回成功响应，不带数据
     *
     * @param <T> 泛型类型
     * @return 成功响应对象
     */
    public static <T> R<T> ok() {
        return setResult(ResultCodeEnum.SUCCESS);
    }

    /**
     * 返回成功响应，带自定义消息
     *
     * @param msg 自定义消息
     * @param <T> 泛型类型
     * @return 成功响应对象
     */
    public static <T> R<T> ok(String msg) {
        return (R<T>) setResult(ResultCodeEnum.SUCCESS).msg(msg);
    }

    /**
     * 返回成功响应，带自定义状态码和消息
     *
     * @param code 自定义状态码
     * @param msg  自定义消息
     * @param <T>  泛型类型
     * @return 成功响应对象
     */
    public static <T> R<T> ok(Integer code, String msg) {
        return (R<T>) setResult(ResultCodeEnum.SUCCESS)
                .code(code)
                .msg(msg);
    }

    /**
     * 返回失败响应，不带数据
     *
     * @param <T> 泛型类型
     * @return 失败响应对象
     */
    public static <T> R<T> fail() {
        return setResult(ResultCodeEnum.FAIL);
    }

    /**
     * 返回失败响应，带自定义消息
     *
     * @param msg 自定义消息
     * @param <T> 泛型类型
     * @return 失败响应对象
     */
    public static <T> R<T> fail(String msg) {
        return (R<T>) setResult(ResultCodeEnum.FAIL).msg(msg);
    }

    /**
     * 返回失败响应，带自定义状态码和消息
     *
     * @param code 自定义状态码
     * @param msg  自定义消息
     * @param <T>  泛型类型
     * @return 失败响应对象
     */
    public static <T> R<T> fail(Integer code, String msg) {
        return (R<T>) setResult(ResultCodeEnum.FAIL)
                .code(code)
                .msg(msg);
    }

    /**
     * 返回错误响应，不带数据
     *
     * @param <T> 泛型类型
     * @return 错误响应对象
     */
    public static <T> R<T> error() {
        return setResult(ResultCodeEnum.SERVER_ERROR);
    }

    /**
     * 返回错误响应，带自定义消息
     *
     * @param msg 自定义消息
     * @param <T> 泛型类型
     * @return 错误响应对象
     */
    public static <T> R<T> error(String msg) {
        return (R<T>) setResult(ResultCodeEnum.SERVER_ERROR).msg(msg);
    }

    /**
     * 返回错误响应，带自定义状态码和消息
     *
     * @param code 自定义状态码
     * @param msg  自定义消息
     * @param <T>  泛型类型
     * @return 错误响应对象
     */
    public static <T> R<T> error(Integer code, String msg) {
        return (R<T>) setResult(ResultCodeEnum.SERVER_ERROR)
                .code(code)
                .msg(msg);
    }

    /**
     * 自定义状态码和消息
     *
     * @param code 自定义状态码
     * @param msg  自定义消息
     * @param <T>  泛型类型
     * @return 响应对象
     */
    public static <T> R<T> custom(Integer code, String msg) {
        return new R<T>()
                .code(code)
                .msg(msg);
    }

    /**
     * 根据枚举类设置响应结果
     *
     * @param resultCodeEnum 结果枚举类
     * @param <T>            泛型类型
     * @return 响应对象
     */
    public static <T> R<T> setResult(ResultCodeEnum resultCodeEnum) {
        return new R<T>()
                .code(resultCodeEnum.getCode())
                .msg(resultCodeEnum.getMsg());
    }

    /**
     * 设置响应数据
     *
     * @param data 响应数据
     * @return 当前对象
     */
    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    /**
     * 设置响应数据为 List 类型
     *
     * @param list 响应数据列表
     * @return 当前对象
     */
    public R<T> data(List<?> list) {
        return data((T) list);
    }

    /**
     * 设置响应数据为 Map 类型
     *
     * @param map 响应数据 Map
     * @return 当前对象
     */
    public R<T> data(Map<String, ?> map) {
        return data((T) map);
    }

    /**
     * 设置响应数据为单个 Key-Value 对
     *
     * @param key   键
     * @param value 值
     * @return 当前对象
     */
    public R<T> data(String key, Object value) {
        return data((T) new HashMap<String, Object>() {{
            put(key, value);
        }});
    }

    /**
     * 设置响应数据为单个 Key 和 Map 的组合
     *
     * @param key 键
     * @param map 值（Map 类型）
     * @return 当前对象
     */
    public R<T> data(String key, Map<String, ?> map) {
        return data((T) new HashMap<String, Object>() {{
            put(key, map);
        }});
    }

    /**
     * 设置响应数据为多个 Key-Value 对（可变参数）
     *
     * @param entries Key-Value 对数组
     * @return 当前对象
     * @throws IllegalArgumentException 如果提供的参数不是成对的
     */
    public R<T> data(Object... entries) {
        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("Key-Value pairs must be provided in even numbers.");
        }
        Map<String, Object> resultMap = new HashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            resultMap.put(entries[i].toString(), entries[i + 1]);
        }
        return data((T) resultMap);
    }
}