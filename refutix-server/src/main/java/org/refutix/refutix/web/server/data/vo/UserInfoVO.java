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

package org.refutix.refutix.web.server.data.vo;

import cn.dev33.satoken.stp.SaTokenInfo;
import lombok.Data;
import org.refutix.refutix.web.server.data.model.Menu;
import org.refutix.refutix.web.server.data.model.Role;
import org.refutix.refutix.web.server.data.model.Tenant;
import org.refutix.refutix.web.server.data.model.User;

import java.util.List;

/** user data transfer object. */
@Data
public class UserInfoVO {

    /** current user info. */
    private User user;

    /** current user's tenant list. */
    private List<Tenant> tenantList;

    /** current user's role list. */
    private List<Role> roleList;

    /** current user's token info. */
    private SaTokenInfo saTokenInfo;

    /** current user's menu list. */
    private List<Menu> menuList;

    /** current user's tenant. */
    private Tenant currentTenant;
}
