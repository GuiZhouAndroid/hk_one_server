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
 * @ClassName: Role
 * @Description: 角色实体类
 * @Author: 张松
 * @Date: 2025/12/8 20:12
 * @Version: 1.0
 */
@Getter
@Setter
@ToString
@TableName("role")
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色类型：1-系统角色 2-仓库角色
     */
    private Byte roleType;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：1-正常 0-禁用
     */
    private Byte status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}