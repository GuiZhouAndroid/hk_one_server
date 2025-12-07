package com.zhangsong.hk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangsong.hk.entity.Role;

import java.util.List;

/**
 * @ClassName: IRoleService
 * @Description: 角色服务接口
 * @Author: 张松
 * @Date: 2025/12/8 20:12
 * @Version: 1.0
 */
public interface IRoleService extends IService<Role> {
    
    /**
     * 根据角色编码获取角色
     * @param roleCode 角色编码
     * @return 角色对象
     */
    Role getRoleByCode(String roleCode);
    
    /**
     * 获取用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(Integer userId);
    
    /**
     * 获取所有可用角色
     * @return 可用角色列表
     */
    List<Role> getAllActiveRoles();
    
    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否分配成功
     */
    boolean assignRoleToUser(Integer userId, Integer roleId);
    
    /**
     * 为用户分配多个角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否分配成功
     */
    boolean assignRolesToUser(Integer userId, List<Integer> roleIds);
    
    /**
     * 移除用户的角色
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否移除成功
     */
    boolean removeRoleFromUser(Integer userId, Integer roleId);
}