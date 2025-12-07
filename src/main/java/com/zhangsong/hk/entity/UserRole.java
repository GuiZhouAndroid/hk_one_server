package com.zhangsong.hk.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName: UserRole
 * @Description: 用户角色关联实体类
 * @Author: 张松
 * @Date: 2025/12/8 20:12
 * @Version: 1.0
 */
@Getter
@Setter
@ToString
@TableName("user_role")
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 角色ID
     */
    private Integer roleId;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}