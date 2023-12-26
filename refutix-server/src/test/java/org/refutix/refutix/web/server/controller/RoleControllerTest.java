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

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.refutix.refutix.web.server.data.model.Role;
import org.refutix.refutix.web.server.data.result.PageR;
import org.refutix.refutix.web.server.data.result.R;
import org.refutix.refutix.web.server.util.ObjectMapperUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Test for SysRoleController. */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoleControllerTest extends ControllerTestBase {

    private static final String rolePath = "/api/role";

    private static final int roleId = 3;
    private static final String roleName = "test";

    @Test
    @Order(1)
    public void testAddRole() throws Exception {
        Role role = new Role();
        role.setId(roleId);
        role.setRoleName(roleName);
        role.setRoleKey(roleName);
        role.setSort(3);
        role.setEnabled(true);
        role.setIsDelete(false);
        role.setRemark(roleName);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(rolePath)
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(role))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @Order(2)
    public void testQueryRole() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(rolePath + "/" + roleId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Role> r =
                ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Role>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals(r.getData().getRoleName(), roleName);
    }

    @Test
    @Order(3)
    public void testEditRole() throws Exception {
        String newRoleName = roleName + "-edit";
        Role role = new Role();
        role.setId(roleId);
        role.setRoleName(newRoleName);
        role.setRoleKey(newRoleName);
        role.setSort(3);
        role.setEnabled(true);
        role.setIsDelete(false);
        role.setRemark(newRoleName);

        mockMvc.perform(
                        MockMvcRequestBuilders.put(rolePath)
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(role))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(rolePath + "/" + roleId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Role> r =
                ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Role>>() {});
        assertEquals(200, r.getCode());
        assertNotNull(r.getData());
        assertEquals(r.getData().getRoleName(), newRoleName);
    }

    @Test
    @Order(5)
    public void testDeleteRole() throws Exception {
        String delResponseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(rolePath + "/" + roleId)
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<?> result = ObjectMapperUtils.fromJSON(delResponseString, R.class);
        assertEquals(200, result.getCode());
    }

    @Test
    @Order(4)
    public void testGetRoleList() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(rolePath + "/list")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        PageR<?> r = ObjectMapperUtils.fromJSON(responseString, PageR.class);
        assertNotNull(r);
        assertTrue(
                r.getData() != null
                        && ((r.getTotal() > 0 && r.getData().size() > 0)
                                || (r.getTotal() == 0 && r.getData().size() == 0)));
    }
}
