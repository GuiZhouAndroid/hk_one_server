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
    @Size(min = 6, max = 20, message = "密码长度需为6-20位")
    private String password;
    
    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 10, message = "真实姓名长度需为2-10位")
    private String realName;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
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