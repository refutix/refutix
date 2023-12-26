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

package org.refutix.refutix.web.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.refutix.refutix.web.server.data.model.User;

import java.util.List;

/** User table mapper. */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * Query user list.
     *
     * @param user query params
     * @return user list
     */
    List<User> selectUserList(User user);

    /**
     * Query user list by role ID.
     *
     * @param user query params
     * @return user list
     */
    List<User> selectAllocatedList(User user);

    /**
     * Query the list of unassigned user roles.
     *
     * @param user query params
     * @return user list
     */
    List<User> selectUnallocatedList(User user);

    /**
     * Query user info by username.
     *
     * @param username username
     * @return user info
     */
    User selectUserByUserName(String username);

    /**
     * Query user info by user ID.
     *
     * @param userId user ID
     * @return user info
     */
    User selectUserById(Integer userId);
}
