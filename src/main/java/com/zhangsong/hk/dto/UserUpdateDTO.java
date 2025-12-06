package com.zhangsong.hk.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateDTO {
    
    @NotNull(message = "用户ID不能为空")
    private Integer id;
    
    @Size(min = 2, max = 10, message = "真实姓名长度需为2-10位")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    @Pattern(
            regexp = "^((\\+\\d{1,5})?1[3-9]\\d{9}|\\+[1-9]\\d{0,4}[0-9\\s]{6,14})$",
            message = "手机号格式不正确"
    )
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