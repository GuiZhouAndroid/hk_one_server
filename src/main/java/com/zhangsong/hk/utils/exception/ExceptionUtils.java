package com.zhangsong.hk.utils.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 */
public class ExceptionUtils {

    /**
     * 获取异常的完整堆栈信息
     */
    public static String getStackTrace(Throwable e) {
        if (e == null) {
            return "";
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * 获取异常的根本原因信息
     * 如果 ExceptionUtils 中没有这个方法，就添加它
     */
    public static String getRootCauseMessage(Throwable e) {
        if (e == null) {
            return "";
        }

        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage();
    }

    /**
     * 获取异常的根本原因
     */
    public static Throwable getRootCause(Throwable e) {
        if (e == null) {
            return null;
        }

        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}
