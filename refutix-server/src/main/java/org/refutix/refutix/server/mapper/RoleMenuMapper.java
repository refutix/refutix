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

package org.refutix.refutix.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.refutix.refutix.server.data.model.RoleMenuRel;

import java.util.List;

/** role-menu mapper. */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenuRel> {
    /**
     * Query menu usage quantity.
     *
     * @param menuId menu ID
     * @return result
     */
    int checkMenuExistRole(Integer menuId);

    /**
     * Delete roles and menu associations through role ID.
     *
     * @param roleId role ID
     * @return result
     */
    int deleteRoleMenuByRoleId(Integer roleId);

    /**
     * Batch delete role menu association information.
     *
     * @param roleIds roleIds
     * @return result
     */
    int deleteRoleMenu(Integer[] roleIds);

    /**
     * Batch Add Role Menu Information.
     *
     * @param roleMenuRelList role-menu List
     * @return result
     */
    int batchRoleMenu(List<RoleMenuRel> roleMenuRelList);

    /**
     * Query the menu permissions that users have.
     *
     * @param userId user ID
     * @return result
     */
    List<RoleMenuRel> queryRoleMenuByUser(Integer userId);
}
