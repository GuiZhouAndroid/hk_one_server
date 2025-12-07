package com.zhangsong.hk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangsong.hk.entity.Role;
import com.zhangsong.hk.entity.UserRole;
import com.zhangsong.hk.mapper.RoleMapper;
import com.zhangsong.hk.mapper.UserRoleMapper;
import com.zhangsong.hk.service.IRoleService;
import com.zhangsong.hk.utils.exception.CustomException;
import com.zhangsong.hk.utils.response.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: RoleServiceImpl
 * @Description: 角色服务实现类
 * @Author: 张松
 * @Date: 2025/12/8 20:12
 * @Version: 1.0
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 根据角色编码获取角色
     */
    @Override
    public Role getRoleByCode(String roleCode) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("role_code", roleCode)
               .eq("status", 1);
        return this.getOne(wrapper);
    }

    /**
     * 获取用户的所有角色
     */
    @Override
    public List<Role> getRolesByUserId(Integer userId) {
        // 先查询用户角色关联表
        QueryWrapper<UserRole> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.eq("user_id", userId);
        List<UserRole> userRoles = userRoleMapper.selectList(userRoleWrapper);
        
        if (CollectionUtils.isEmpty(userRoles)) {
            return List.of();
        }
        
        // 提取角色ID
        List<Integer> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        // 查询角色信息
        QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
        roleWrapper.in("id", roleIds)
                   .eq("status", 1);
        return this.list(roleWrapper);
    }

    /**
     * 获取所有可用角色
     */
    @Override
    public List<Role> getAllActiveRoles() {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1)
               .orderByAsc("id");
        return this.list(wrapper);
    }

    /**
     * 为用户分配角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoleToUser(Integer userId, Integer roleId) {
        // 检查角色是否存在
        Role role = this.getById(roleId);
        if (role == null || role.getStatus() == 0) {
            throw new CustomException(ResultCodeEnum.NOT_FOUND.getCode(), "角色不存在或已禁用");
        }
        
        // 检查是否已分配
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("role_id", roleId);
        UserRole exist = userRoleMapper.selectOne(wrapper);
        
        if (exist != null) {
            return true; // 已存在，直接返回成功
        }
        
        // 创建关联
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRole.setCreatedTime(LocalDateTime.now());
        
        return userRoleMapper.insert(userRole) > 0;
    }

    /**
     * 为用户分配多个角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(Integer userId, List<Integer> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return true;
        }
        
        for (Integer roleId : roleIds) {
            boolean success = assignRoleToUser(userId, roleId);
            if (!success) {
                throw new CustomException(ResultCodeEnum.FAIL.getCode(), "分配角色失败");
            }
        }
        return true;
    }

    /**
     * 移除用户的角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRoleFromUser(Integer userId, Integer roleId) {
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("role_id", roleId);
        return userRoleMapper.delete(wrapper) > 0;
    }
}