package com.zhangsong.hk.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateDTO {
    
    @NotNull(message = "用户ID不能为空")
    private Integer id;
    
    @Size(min = 2, max = 10, message = "真实姓名长度需为2-10位")
    private String realName;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Min(value = 0, message = "性别值不正确")
    @Max(value = 2, message = "性别值不正确")
    private Byte gender;
    
    @Min(value = 0, message = "配送员标识不正确")
    @Max(value = 1, message = "配送员标识不正确")
    private Byte isDelivery;
    
    @Min(value = 0, message = "状态值不正确")
    @Max(value = 1, message = "状态值不正确")
    private Byte status;
}