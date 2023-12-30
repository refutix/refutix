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

package org.refutix.refutix.server.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import org.refutix.refutix.server.constant.Constants;
import org.refutix.refutix.server.data.model.Menu;
import org.refutix.refutix.server.data.result.R;
import org.refutix.refutix.server.data.result.enums.Status;
import org.refutix.refutix.server.data.tree.TreeSelect;
import org.refutix.refutix.server.data.vo.RoleMenuTreeselectVO;
import org.refutix.refutix.server.data.vo.RouterVO;
import org.refutix.refutix.server.service.SysMenuService;
import org.refutix.refutix.server.util.StringUtils;
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

/** menu controller. */
@RestController
@RequestMapping("/api/menu")
public class SysMenuController {
    @Autowired private SysMenuService menuService;

    /** Get menu list. */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    public R<List<Menu>> list(Menu menu) {
        List<Menu> menus = menuService.selectMenuList(menu);
        return R.succeed(menus);
    }

    /** Get menu info by menuId. */
    @SaCheckPermission("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public R<Menu> getInfo(@PathVariable Integer menuId) {
        return R.succeed(menuService.selectMenuById(menuId));
    }

    /** Get menu drop-down tree list. */
    @GetMapping("/treeselect")
    public R<List<TreeSelect>> treeselect(Menu menu) {
        List<Menu> menus = menuService.selectMenuList(menu);
        return R.succeed(menuService.buildMenuTreeSelect(menus));
    }

    /** Load the corresponding character menu list tree. */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public R<RoleMenuTreeselectVO> roleMenuTreeselect(@PathVariable("roleId") Integer roleId) {
        List<Menu> menus = menuService.selectMenuList();

        List<TreeSelect> treeMenus = menuService.buildMenuTreeSelect(menus);
        List<Integer> checkedKeys = menuService.selectMenuListByRoleId(roleId);
        return R.succeed(new RoleMenuTreeselectVO(checkedKeys, treeMenus));
    }

    /** add new menu. */
    @SaCheckPermission("system:menu:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody Menu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.failed(Status.MENU_NAME_IS_EXIST, menu.getMenuName());
        } else if (Constants.YES_FRAME == menu.getIsFrame()
                && !StringUtils.isHttp(menu.getPath())) {
            return R.failed(Status.MENU_PATH_INVALID, menu.getPath());
        }
        return menuService.insertMenu(menu) ? R.succeed() : R.failed();
    }

    /** update menu. */
    @SaCheckPermission("system:menu:edit")
    @PutMapping
    public R<Void> edit(@Validated @RequestBody Menu menu) {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.failed(Status.MENU_NAME_IS_EXIST, menu.getMenuName());
        } else if (Constants.YES_FRAME == menu.getIsFrame()
                && !StringUtils.isHttp(menu.getPath())) {
            return R.failed(Status.MENU_PATH_INVALID, menu.getPath());
        }
        return menuService.updateMenu(menu) ? R.succeed() : R.failed();
    }

    /** delete menu. */
    @SaCheckPermission("system:menu:remove")
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable("menuId") Integer menuId) {
        if (menuService.hasChildByMenuId(menuId) || menuService.checkMenuExistRole(menuId)) {
            return R.failed(Status.MENU_IN_USED);
        }
        return menuService.deleteMenuById(menuId) ? R.succeed() : R.failed();
    }

    /** Get router list. */
    @GetMapping("/getRouters")
    public R<List<RouterVO>> getRouters() {
        int userId = StpUtil.getLoginIdAsInt();
        List<Menu> menus = menuService.selectMenuTreeByUserId(userId);
        return R.succeed(menuService.buildMenus(menus));
    }
}
