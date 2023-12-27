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

package org.refutix.refutix.web.server.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.refutix.refutix.web.server.data.model.Role;
import org.refutix.refutix.web.server.data.model.User;
import org.refutix.refutix.web.server.data.model.UserRole;
import org.refutix.refutix.web.server.data.result.PageR;
import org.refutix.refutix.web.server.data.result.R;
import org.refutix.refutix.web.server.data.result.enums.Status;
import org.refutix.refutix.web.server.service.SysRoleService;
import org.refutix.refutix.web.server.service.UserService;
import org.refutix.refutix.web.server.util.PageSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** role controller. */
@RestController
@RequestMapping("/api/role")
public class SysRoleController {
    @Autowired private SysRoleService roleService;

    @Autowired private UserService userService;

    @SaCheckPermission("system:role:list")
    @GetMapping("/list")
    public PageR<Role> list(Role role) {
        IPage<Role> page = PageSupport.startPage();
        List<Role> list = roleService.selectRoleList(page, role);
        return PageR.<Role>builder().success(true).total(page.getTotal()).data(list).build();
    }

    /** Obtain detailed information based on role number. */
    @SaCheckPermission("system:role:query")
    @GetMapping(value = "/{roleId}")
    public R<Role> getInfo(@PathVariable Integer roleId) {
        return R.succeed(roleService.selectRoleById(roleId));
    }

    /** Add new role. */
    @SaCheckPermission("system:role:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody Role role) {
        if (!roleService.checkRoleNameUnique(role)) {
            return R.failed(Status.ROLE_NAME_IS_EXIST, role.getRoleName());
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.failed(Status.ROLE_KEY_IS_EXIST, role.getRoleKey());
        }

        return roleService.insertRole(role) > 0 ? R.succeed() : R.failed();
    }

    /** Update role info. */
    @SaCheckPermission("system:role:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody Role role) {
        roleService.checkRoleAllowed(role);
        if (!roleService.checkRoleNameUnique(role)) {
            return R.failed(Status.ROLE_NAME_IS_EXIST, role.getRoleName());
        } else if (!roleService.checkRoleKeyUnique(role)) {
            return R.failed(Status.ROLE_KEY_IS_EXIST, role.getRoleKey());
        }

        if (roleService.updateRole(role) > 0) {
            // TODO update user permissions cache
            return R.succeed();
        }
        return R.failed();
    }

    /** Update role status. */
    @SaCheckPermission("system:role:edit")
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody Role role) {
        roleService.checkRoleAllowed(role);
        return roleService.updateRoleStatus(role) ? R.succeed() : R.failed();
    }

    /** Delete role. */
    @SaCheckPermission("system:role:remove")
    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable Integer[] roleIds) {
        return roleService.deleteRoleByIds(roleIds) > 0 ? R.succeed() : R.failed();
    }

    /** Obtain a list of role selection boxes. */
    @SaCheckPermission("system:role:query")
    @GetMapping("/all")
    public R<List<Role>> all() {
        return R.succeed(roleService.list());
    }

    /** Query the list of assigned user roles. */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/allocatedList")
    public PageR<User> allocatedList(User user) {
        IPage<Role> page = PageSupport.startPage();
        List<User> list = userService.selectAllocatedList(user);
        return PageR.<User>builder().success(true).total(page.getTotal()).data(list).build();
    }

    /** Query the list of unassigned user roles. */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/unallocatedList")
    public PageR<User> unallocatedList(User user) {
        IPage<Role> page = PageSupport.startPage();
        List<User> list = userService.selectUnallocatedList(user);
        return PageR.<User>builder().success(true).total(page.getTotal()).data(list).build();
    }

    /** Unauthorize User. */
    @SaCheckPermission("system:role:edit")
    @PutMapping("/authUser/cancel")
    public R<Void> cancelAuthUser(@RequestBody UserRole userRole) {
        return roleService.deleteAuthUser(userRole) > 0 ? R.succeed() : R.failed();
    }

    /** Batch Unauthorize User. */
    @SaCheckPermission("system:role:edit")
    @PutMapping("/authUser/cancelAll")
    public R<Void> cancelAuthUserAll(Integer roleId, Integer[] userIds) {
        return roleService.deleteAuthUsers(roleId, userIds) > 0 ? R.succeed() : R.failed();
    }

    /** Batch Set User Authorization. */
    @SaCheckPermission("system:role:edit")
    @PutMapping("/authUser/selectAll")
    public R<Void> selectAuthUserAll(Integer roleId, Integer[] userIds) {
        return roleService.insertAuthUsers(roleId, userIds) > 0 ? R.succeed() : R.failed();
    }
}
