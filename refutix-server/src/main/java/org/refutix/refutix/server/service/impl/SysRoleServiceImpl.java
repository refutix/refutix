/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.refutix.refutix.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Preconditions;
import org.refutix.refutix.server.data.model.Role;
import org.refutix.refutix.server.data.model.RoleMenuRel;
import org.refutix.refutix.server.data.model.UserRole;
import org.refutix.refutix.server.data.result.exception.role.RoleInUsedException;
import org.refutix.refutix.server.mapper.RoleMenuMapper;
import org.refutix.refutix.server.mapper.SysRoleMapper;
import org.refutix.refutix.server.mapper.UserRoleMapper;
import org.refutix.refutix.server.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Role Service. */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, Role> implements SysRoleService {

    @Autowired private SysRoleMapper roleMapper;

    @Autowired private RoleMenuMapper roleMenuMapper;

    @Autowired private UserRoleMapper userRoleMapper;

    /**
     * Query role list.
     *
     * @param role query params
     * @return role list
     */
    @Override
    public List<Role> selectRoleList(IPage<Role> page, Role role) {
        return roleMapper.selectRoleList(page, role);
    }

    /**
     * Query role list by user ID.
     *
     * @param userId user ID
     * @return role list
     */
    @Override
    public List<Role> selectRolesByUserId(Integer userId) {
        List<Role> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        List<Role> roles = this.list();
        for (Role role : roles) {
            for (Role userRole : userRoles) {
                if (role.getId().intValue() == userRole.getId().intValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * Query role permission by user ID.
     *
     * @param userId user ID
     * @return permission list
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Integer userId) {
        List<Role> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (Role perm : perms) {
            if (perm != null) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * Query role list by user ID.
     *
     * @param userId user ID
     * @return role IDs
     */
    @Override
    public List<Integer> selectRoleListByUserId(Integer userId) {
        return roleMapper.selectRoleListByUserId(userId);
    }

    /**
     * Query role info by role ID.
     *
     * @param roleId role ID
     * @return role info
     */
    @Override
    public Role selectRoleById(Integer roleId) {
        return this.getById(roleId);
    }

    /**
     * Verify if the role name is unique.
     *
     * @param role role info
     * @return result
     */
    @Override
    public boolean checkRoleNameUnique(Role role) {
        int roleId = role.getId() == null ? -1 : role.getId();
        Role info = this.lambdaQuery().eq(Role::getRoleName, role.getRoleName()).one();
        return info == null || info.getId() == roleId;
    }

    /**
     * Verify whether role permissions are unique.
     *
     * @param role role info
     * @return result
     */
    @Override
    public boolean checkRoleKeyUnique(Role role) {
        int roleId = role.getId() == null ? -1 : role.getId();
        Role info = this.lambdaQuery().eq(Role::getRoleKey, role.getRoleKey()).one();
        return info == null || info.getId() == roleId;
    }

    /**
     * Verify whether the role allows operations.
     *
     * @param role role info
     */
    @Override
    public boolean checkRoleAllowed(Role role) {
        return role.getId() != null && role.getId() == 1;
    }

    /**
     * Add role.
     *
     * @param role role info
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(Role role) {
        this.save(role);
        return insertRoleMenu(role);
    }

    /**
     * Update role.
     *
     * @param role role info
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(Role role) {
        this.updateById(role);
        roleMenuMapper.deleteRoleMenuByRoleId(role.getId());
        return insertRoleMenu(role);
    }

    /**
     * Add role-menu association information.
     *
     * @param role role info
     */
    public int insertRoleMenu(Role role) {
        int rows = 1;
        if (role.getMenuIds() != null && role.getMenuIds().length > 0) {
            List<RoleMenuRel> list = new ArrayList<RoleMenuRel>();
            for (Integer menuId : role.getMenuIds()) {
                RoleMenuRel rm = new RoleMenuRel();
                rm.setRoleId(role.getId());
                rm.setMenuId(menuId);
                list.add(rm);
            }
            if (list.size() > 0) {
                rows = roleMenuMapper.batchRoleMenu(list);
            }
        }
        return rows;
    }

    /**
     * Delete role.
     *
     * @param roleId role ID
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleById(Integer roleId) {
        roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        return roleMapper.deleteById(roleId);
    }

    /**
     * Batch delete role.
     *
     * @param roleIds role IDs
     * @return result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(Integer[] roleIds) {
        for (Integer roleId : roleIds) {
            Role sysRole = new Role();
            sysRole.setId(roleId);
            checkRoleAllowed(sysRole);
            Role role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw new RoleInUsedException(role.getRoleName());
            }
        }
        roleMenuMapper.deleteRoleMenu(roleIds);
        return roleMapper.deleteBatchIds(Arrays.asList(roleIds));
    }

    /**
     * Unauthorize user role.
     *
     * @param userRole user-role association
     * @return result
     */
    @Override
    public int deleteAuthUser(UserRole userRole) {
        return userRoleMapper.deleteById(userRole);
    }

    /**
     * Batch unauthorize user role.
     *
     * @param roleId role ID
     * @param userIds user IDs
     * @return result
     */
    @Override
    public int deleteAuthUsers(Integer roleId, Integer[] userIds) {
        return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
    }

    /**
     * Batch add role-menu association information.
     *
     * @param roleId role ID
     * @param userIds user IDs
     * @return result
     */
    @Override
    public int insertAuthUsers(Integer roleId, Integer[] userIds) {
        List<UserRole> list = new ArrayList<UserRole>();
        for (Integer userId : userIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        return userRoleMapper.batchUserRole(list);
    }

    /**
     * Query the number of roles used by role ID.
     *
     * @param roleId role ID
     * @return result
     */
    @Override
    public int countUserRoleByRoleId(Integer roleId) {
        return userRoleMapper
                .selectCount(new QueryWrapper<UserRole>().eq("role_id", roleId))
                .intValue();
    }

    @Override
    public boolean updateRoleStatus(Role role) {
        Preconditions.checkArgument(role != null && role.getId() != null);
        return this.lambdaUpdate()
                .set(Role::getEnabled, role.getEnabled())
                .eq(Role::getId, role.getId())
                .update();
    }
}
