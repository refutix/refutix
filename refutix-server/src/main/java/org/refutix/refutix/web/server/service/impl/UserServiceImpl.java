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

package org.refutix.refutix.web.server.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.refutix.refutix.web.server.data.dto.LoginDTO;
import org.refutix.refutix.web.server.data.enums.UserType;
import org.refutix.refutix.web.server.data.model.Menu;
import org.refutix.refutix.web.server.data.model.Role;
import org.refutix.refutix.web.server.data.model.RoleMenuRel;
import org.refutix.refutix.web.server.data.model.User;
import org.refutix.refutix.web.server.data.model.UserRole;
import org.refutix.refutix.web.server.data.result.exception.BaseException;
import org.refutix.refutix.web.server.data.result.exception.user.UserDisabledException;
import org.refutix.refutix.web.server.data.result.exception.user.UserNotExistsException;
import org.refutix.refutix.web.server.data.result.exception.user.UserPasswordNotMatchException;
import org.refutix.refutix.web.server.data.vo.UserInfoVO;
import org.refutix.refutix.web.server.mapper.UserMapper;
import org.refutix.refutix.web.server.service.LdapService;
import org.refutix.refutix.web.server.service.RoleMenuService;
import org.refutix.refutix.web.server.service.SysMenuService;
import org.refutix.refutix.web.server.service.SysRoleService;
import org.refutix.refutix.web.server.service.TenantService;
import org.refutix.refutix.web.server.service.UserRoleService;
import org.refutix.refutix.web.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** UserServiceImpl. */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired private LdapService ldapService;
    @Autowired private UserMapper userMapper;
    @Autowired private UserRoleService userRoleService;
    @Autowired private SysRoleService sysRoleService;
    @Autowired private RoleMenuService roleMenuService;
    @Autowired private SysMenuService sysMenuService;
    @Autowired private TenantService tenantService;

    /**
     * login by username and password.
     *
     * @param loginDTO login info
     * @return {@link String}
     */
    @Override
    public UserInfoVO login(LoginDTO loginDTO) throws BaseException {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        User user =
                loginDTO.isLdapLogin()
                        ? ldapLogin(username, password)
                        : localLogin(username, password);
        if (!user.getEnabled()) {
            throw new UserDisabledException();
        }
        // query user info
        UserInfoVO userInfoVo = getUserInfoVo(user);
        // todo: Currently do not bind tenants
        /*if (CollectionUtils.isEmpty(userInfoVo.getTenantList())) {
            throw new UserNotBindTenantException();
        }*/

        StpUtil.login(user.getId(), loginDTO.isRememberMe());

        return userInfoVo;
    }

    /**
     * get user info. include user, role, menu. tenant.
     *
     * @param user user
     * @return {@link UserInfoVO}
     */
    private UserInfoVO getUserInfoVo(User user) {
        UserInfoVO userInfoVo = new UserInfoVO();
        userInfoVo.setUser(user);
        userInfoVo.setSaTokenInfo(StpUtil.getTokenInfo());

        // get user role list
        List<Role> roles = new ArrayList<>();
        List<UserRole> userRoleList = userRoleService.selectUserRoleListByUserId(user);

        // get role list
        userRoleList.forEach(
                userRole -> {
                    roles.add(sysRoleService.getById(userRole.getRoleId()));
                });
        userInfoVo.setRoleList(roles);
        // get menu list
        List<Menu> menus = new ArrayList<>();
        userRoleList.forEach(
                userRole -> {
                    roleMenuService
                            .list(
                                    new LambdaQueryWrapper<RoleMenuRel>()
                                            .eq(RoleMenuRel::getRoleId, userRole.getRoleId()))
                            .forEach(
                                    roleMenu -> {
                                        menus.add(sysMenuService.getById(roleMenu.getMenuId()));
                                    });
                });
        userInfoVo.setMenuList(menus);

        userInfoVo.setCurrentTenant(tenantService.getById(1));
        return userInfoVo;
    }

    private User localLogin(String username, String password) throws BaseException {
        User user =
                this.lambdaQuery()
                        .eq(User::getUsername, username)
                        .eq(User::getUserType, UserType.LOCAL.getCode())
                        .one();
        if (user == null) {
            throw new UserNotExistsException();
        }

        if (SaSecureUtil.md5(password).equals(user.getPassword())) {
            return user;
        } else {
            throw new UserPasswordNotMatchException();
        }
    }

    private User ldapLogin(String username, String password) throws BaseException {
        Optional<User> authenticate = ldapService.authenticate(username, password);
        if (!authenticate.isPresent()) {
            throw new UserPasswordNotMatchException();
        }

        User user =
                this.lambdaQuery()
                        .eq(User::getUsername, username)
                        .eq(User::getUserType, UserType.LDAP.getCode())
                        .one();
        if (user == null) {
            user = authenticate.get();
            this.save(user);
            // TODO assign default roles and tenants
        }
        return user;
    }

    /**
     * Query the list of assigned user roles.
     *
     * @param user query params
     * @return user list
     */
    @Override
    public List<User> selectAllocatedList(User user) {
        return userMapper.selectAllocatedList(user);
    }

    /**
     * Query the list of unassigned user roles.
     *
     * @param user query params
     * @return user list
     */
    @Override
    public List<User> selectUnallocatedList(User user) {
        return userMapper.selectUnallocatedList(user);
    }
}
