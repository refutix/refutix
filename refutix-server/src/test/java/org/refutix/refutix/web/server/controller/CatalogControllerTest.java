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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.refutix.refutix.web.server.data.dto.CatalogDTO;
import org.refutix.refutix.web.server.data.result.R;
import org.refutix.refutix.web.server.util.ObjectMapperUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test for CatalogController. */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CatalogControllerTest extends ControllerTestBase {

    private static final String catalogPath = "/api/catalog";

    private static final String catalogName = "testCatalog";

    @Test
    public void testCreateCatalog() throws Exception {
        CatalogDTO catalog = new CatalogDTO();
        catalog.setType("filesystem");
        catalog.setName(catalogName);
        catalog.setWarehouse(tempFile.toUri().toString());
        catalog.setDelete(false);

        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(catalogPath + "/create")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(catalog))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, r.getCode());

        CatalogDTO removeCatalog = new CatalogDTO();
        removeCatalog.setId(1);
        removeCatalog.setName(catalogName);

        mockMvc.perform(
                        MockMvcRequestBuilders.post(catalogPath + "/remove")
                                .cookie(cookie)
                                .content(ObjectMapperUtils.toJSON(removeCatalog))
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        R<Void> remove =
                ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, remove.getCode());
    }

    @Test
    public void testGetAllCatalogs() throws Exception {
        String responseString =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(catalogPath + "/getAllCatalogs")
                                        .cookie(cookie)
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> r = ObjectMapperUtils.fromJSON(responseString, new TypeReference<R<Void>>() {});
        assertEquals(200, r.getCode());
    }
}
