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

package org.refutix.refutix.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.refutix.refutix.server.data.model.Menu;
import org.refutix.refutix.server.data.tree.TreeSelect;
import org.refutix.refutix.server.data.vo.RouterVO;

import java.util.List;
import java.util.Set;

/** Menu service. */
public interface SysMenuService extends IService<Menu> {
    /**
     * Query menu list by user.
     *
     * @return menu list
     */
    List<Menu> selectMenuList();

    /**
     * Query menu list.
     *
     * @param menu query params
     * @return menu list
     */
    List<Menu> selectMenuList(Menu menu);

    /**
     * Query permissions by user ID.
     *
     * @param userId user ID
     * @return permission List
     */
    Set<String> selectMenuPermsByUserId(Integer userId);

    /**
     * Query permissions by role ID.
     *
     * @param roleId role ID
     * @return permission List
     */
    Set<String> selectMenuPermsByRoleId(Integer roleId);

    /**
     * Query menu list by user ID.
     *
     * @param userId user ID
     * @return menu list
     */
    List<Menu> selectMenuTreeByUserId(Integer userId);

    /**
     * Query menu tree information by role ID.
     *
     * @param roleId role ID
     * @return selected menu list
     */
    List<Integer> selectMenuListByRoleId(Integer roleId);

    /**
     * Build router by menu.
     *
     * @param menus menu list
     * @return router list
     */
    List<RouterVO> buildMenus(List<Menu> menus);

    /**
     * Builder menu tree.
     *
     * @param menus menu list
     * @return menu tree
     */
    List<Menu> buildMenuTree(List<Menu> menus);

    /**
     * Builder tree select by menu.
     *
     * @param menus menu list
     * @return menu tree select
     */
    List<TreeSelect> buildMenuTreeSelect(List<Menu> menus);

    /**
     * Query menu info by menu ID.
     *
     * @param menuId menu ID
     * @return menu info
     */
    Menu selectMenuById(Integer menuId);

    /**
     * Is there a menu sub node present.
     *
     * @param menuId menu ID
     * @return result
     */
    boolean hasChildByMenuId(Integer menuId);

    /**
     * Query menu usage quantity.
     *
     * @param menuId menu ID
     * @return result
     */
    boolean checkMenuExistRole(Integer menuId);

    /**
     * Add menu.
     *
     * @param menu menu info
     * @return result
     */
    boolean insertMenu(Menu menu);

    /**
     * Update menu.
     *
     * @param menu menu info
     * @return result
     */
    boolean updateMenu(Menu menu);

    /**
     * Delete menu.
     *
     * @param menuId menu ID
     * @return result
     */
    boolean deleteMenuById(Integer menuId);

    /**
     * Verify if the menu name is unique.
     *
     * @param menu menu info
     * @return result
     */
    boolean checkMenuNameUnique(Menu menu);
}
