package com.zhangsong.hk.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度需为3-20位")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 10, message = "真实姓名长度需为2-10位")
    private String realName;

    /**
     * 用户手机号
     * 支持两种格式：
     * 1. 中国大陆手机号：13800138000 或 +8613800138000
     * - 11位数字，以1开头，第二位是3-9
     * - 可选的+86前缀（国际格式）
     * <p>
     * 2. 国际手机号（含非洲国家）：+2348012345678 或 +254 712 345 678
     * - 以+开头
     * - 国家码1-5位（首位不能为0）
     * - 号码主体6-14位（可包含空格）
     * <p>
     * 正则分解：
     * ^((\+\d{1,5})?1[3-9]\d{9}|          # 中国手机号部分
     * \+[1-9]\d{0,4}[0-9\s]{6,14})$      # 国际手机号部分
     * <p>
     * 示例：
     * ✅ 13800138000       # 中国简写
     * ✅ +8613800138000    # 中国国际格式
     * ✅ +2348012345678    # 尼日利亚
     * ✅ +254 712 345 678  # 肯尼亚（带空格）
     * ❌ 12000000000       # 中国格式但第二位错误
     * ❌ 254712345678      # 缺少+号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(
            regexp = "^((\\+\\d{1,5})?1[3-9]\\d{9}|\\+[1-9]\\d{0,4}[0-9\\s]{6,14})$",
            message = "手机号格式不正确"
    )
    private String phone;

    @NotNull(message = "用户类型不能为空")
    @Min(value = 1, message = "用户类型最小为1")
    @Max(value = 3, message = "用户类型最大为3")
    private Byte userType;

    @Min(value = 0, message = "性别值不正确")
    @Max(value = 2, message = "性别值不正确")
    private Byte gender;

    @Min(value = 0, message = "配送员标识不正确")
    @Max(value = 1, message = "配送员标识不正确")
    private Byte isDelivery;
}