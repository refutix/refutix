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
import org.refutix.refutix.server.data.dto.LoginDTO;
import org.refutix.refutix.server.data.model.User;
import org.refutix.refutix.server.data.result.exception.BaseException;
import org.refutix.refutix.server.data.vo.UserInfoVO;

import java.util.List;

/** User Service. */
public interface UserService extends IService<User> {

    /**
     * login by username and password.
     *
     * @param loginDTO login params
     * @return {@link String}
     */
    UserInfoVO login(LoginDTO loginDTO) throws BaseException;

    /**
     * Query the list of assigned user roles.
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
}
