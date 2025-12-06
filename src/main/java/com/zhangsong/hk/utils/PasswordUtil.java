package com.zhangsong.hk.utils;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.secure.SaSecureUtil;
import org.springframework.stereotype.Component;

/**
 * @ClassName: PasswordUtil
 * @Description: 密码工具类, SaToken 自带
 * @Author: 张松
 * @Date: 2025/12/6 22:44
 * @Version: 1.0
 */
@Component
public class PasswordUtil {

    /**
     * 加密密码 - 使用 BCrypt（自动加盐，最安全）
     */
    public String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 验证密码 - 使用 BCrypt
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    /**
     * 生成默认密码（管理员重置密码时使用）
     */
    public String generateDefaultPassword() {
        return "User@123456";
    }

    /**
     * 使用 MD5 加密（仅用于兼容旧系统）
     */
    public String md5(String text) {
        return SaSecureUtil.md5(text);
    }

    /**
     * 使用 SHA256 加密（可用于敏感数据）
     */
    public String sha256(String text) {
        return SaSecureUtil.sha256(text);
    }
}