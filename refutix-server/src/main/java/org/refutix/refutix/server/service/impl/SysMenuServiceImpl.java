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

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.refutix.refutix.server.data.enums.MenuType;
import org.refutix.refutix.server.data.model.Menu;
import org.refutix.refutix.server.data.model.User;
import org.refutix.refutix.server.data.tree.TreeSelect;
import org.refutix.refutix.server.data.vo.MetaVO;
import org.refutix.refutix.server.data.vo.RouterVO;
import org.refutix.refutix.server.constant.Constants;
import org.refutix.refutix.server.mapper.RoleMenuMapper;
import org.refutix.refutix.server.mapper.SysMenuMapper;
import org.refutix.refutix.server.mapper.SysRoleMapper;
import org.refutix.refutix.server.service.SysMenuService;
import org.refutix.refutix.server.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Menu service. */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, Menu> implements SysMenuService {
    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @Autowired private SysMenuMapper menuMapper;

    @Autowired private SysRoleMapper roleMapper;

    @Autowired private RoleMenuMapper roleMenuMapper;

    /**
     * Query menu list by user.
     *
     * @return menu list
     */
    @Override
    public List<Menu> selectMenuList() {
        return selectMenuList(new Menu());
    }

    /**
     * Query menu list.
     *
     * @param menu query params
     * @return menu list
     */
    @Override
    public List<Menu> selectMenuList(Menu menu) {
        List<Menu> menuList;
        int userId = StpUtil.getLoginIdAsInt();
        if (User.isAdmin(userId)) {
            menuList = menuMapper.selectMenuList(menu);
        } else {
            menuList = menuMapper.selectMenuListByUserId(menu, userId);
        }
        return menuList;
    }

    /**
     * Query permissions by user ID.
     *
     * @param userId user ID
     * @return permission List
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Integer userId) {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * Query permissions by role ID.
     *
     * @param roleId role ID
     * @return permission List
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Integer roleId) {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * Query menu list by user ID.
     *
     * @param userId user ID
     * @return menu list
     */
    @Override
    public List<Menu> selectMenuTreeByUserId(Integer userId) {
        List<Menu> menus = null;
        if (userId != null && userId == 1) {
            menus = menuMapper.selectMenuTreeAll();
        } else {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, 0);
    }

    /**
     * Query menu tree information by role ID.
     *
     * @param roleId role ID
     * @return selected menu list
     */
    @Override
    public List<Integer> selectMenuListByRoleId(Integer roleId) {
        return menuMapper.selectMenuListByRoleId(roleId);
    }

    /**
     * Build router by menu.
     *
     * @param menus menu list
     * @return router list
     */
    @Override
    public List<RouterVO> buildMenus(List<Menu> menus) {
        List<RouterVO> routers = new LinkedList<RouterVO>();
        for (Menu menu : menus) {
            RouterVO router = new RouterVO();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(
                    new MetaVO(
                            menu.getMenuName(),
                            menu.getIcon(),
                            menu.getIsCache() == 1,
                            menu.getPath()));
            List<Menu> cMenus = menu.getChildren();
            if (!CollectionUtils.isEmpty(cMenus) && MenuType.DIR.getType().equals(menu.getType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVO> childrenList = new ArrayList<RouterVO>();
                RouterVO children = new RouterVO();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(
                        new MetaVO(
                                menu.getMenuName(),
                                menu.getIcon(),
                                menu.getIsCache() == 1,
                                menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVO(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVO> childrenList = new ArrayList<RouterVO>();
                RouterVO children = new RouterVO();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(Constants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MetaVO(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * Builder menu tree.
     *
     * @param menus menu list
     * @return menu tree
     */
    @Override
    public List<Menu> buildMenuTree(List<Menu> menus) {
        List<Menu> returnList = new ArrayList<Menu>();
        List<Integer> tempList = menus.stream().map(Menu::getId).collect(Collectors.toList());
        for (Iterator<Menu> iterator = menus.iterator(); iterator.hasNext(); ) {
            Menu menu = (Menu) iterator.next();
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * Builder tree select by menu.
     *
     * @param menus menu list
     * @return menu tree select
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<Menu> menus) {
        List<Menu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * Query menu info by menu ID.
     *
     * @param menuId menu ID
     * @return menu info
     */
    @Override
    public Menu selectMenuById(Integer menuId) {
        return menuMapper.selectMenuById(menuId);
    }

    /**
     * Is there a menu sub node present.
     *
     * @param menuId menu ID
     * @return result
     */
    @Override
    public boolean hasChildByMenuId(Integer menuId) {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * Query menu usage quantity.
     *
     * @param menuId menu ID
     * @return result
     */
    @Override
    public boolean checkMenuExistRole(Integer menuId) {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * Add menu.
     *
     * @param menu menu info
     * @return result
     */
    @Override
    public boolean insertMenu(Menu menu) {
        return this.save(menu);
    }

    /**
     * Update menu.
     *
     * @param menu menu info
     * @return result
     */
    @Override
    public boolean updateMenu(Menu menu) {
        return this.updateById(menu);
    }

    /**
     * Delete menu.
     *
     * @param menuId menu ID
     * @return result
     */
    @Override
    public boolean deleteMenuById(Integer menuId) {
        return this.removeById(menuId);
    }

    /**
     * Verify if the menu name is unique.
     *
     * @param menu menu info
     * @return result
     */
    @Override
    public boolean checkMenuNameUnique(Menu menu) {
        Integer menuId = menu.getId() == null ? -1 : menu.getId();
        Menu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        return info != null && !menuId.equals(info.getId());
    }

    /**
     * Get router name.
     *
     * @param menu menu info
     * @return router name
     */
    public String getRouteName(Menu menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * Get router path.
     *
     * @param menu menu info
     * @return router path
     */
    public String getRouterPath(Menu menu) {
        String routerPath = menu.getPath();
        if (menu.getParentId() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        if (0 == menu.getParentId()
                && MenuType.DIR.getType().equals(menu.getType())
                && Constants.NO_FRAME == menu.getIsFrame()) {
            routerPath = "/" + menu.getPath();
        } else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * Get component information.
     *
     * @param menu menu info
     * @return component info
     */
    public String getComponent(Menu menu) {
        String component = Constants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent())
                && menu.getParentId() != 0
                && isInnerLink(menu)) {
            component = Constants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = Constants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * Is it a menu internal jump.
     *
     * @param menu menu info
     * @return result
     */
    public boolean isMenuFrame(Menu menu) {
        return menu.getParentId() == 0
                && MenuType.MENU.getType().equals(menu.getType())
                && menu.getIsFrame().equals(Constants.NO_FRAME);
    }

    /**
     * Is it an internal chain component.
     *
     * @param menu menu info
     * @return result
     */
    public boolean isInnerLink(Menu menu) {
        return menu.getIsFrame().equals(Constants.NO_FRAME) && StringUtils.isHttp(menu.getPath());
    }

    /**
     * Is parent_view component.
     *
     * @param menu menu info
     * @return result
     */
    public boolean isParentView(Menu menu) {
        return menu.getParentId() != 0 && MenuType.DIR.getType().equals(menu.getType());
    }

    /**
     * Get all child nodes by the parent node ID.
     *
     * @param list menu list
     * @param parentId parent ID
     * @return menu list
     */
    public List<Menu> getChildPerms(List<Menu> list, int parentId) {
        List<Menu> returnList = new ArrayList<Menu>();
        for (Menu t : list) {
            if (t.getParentId() == parentId) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    private void recursionFn(List<Menu> list, Menu t) {
        List<Menu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (Menu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    private List<Menu> getChildList(List<Menu> list, Menu t) {
        List<Menu> tlist = new ArrayList<Menu>();
        for (Menu n : list) {
            if (n.getParentId().equals(t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    private boolean hasChild(List<Menu> list, Menu t) {
        return getChildList(list, t).size() > 0;
    }

    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(
                path,
                new String[] {Constants.HTTP, Constants.HTTPS, Constants.WWW, "."},
                new String[] {"", "", "", "/"});
    }
}
