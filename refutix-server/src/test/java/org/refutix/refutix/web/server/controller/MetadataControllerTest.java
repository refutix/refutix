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
import org.junit.jupiter.api.Test;
import org.refutix.refutix.web.server.data.dto.MetadataDTO;
import org.refutix.refutix.web.server.data.result.R;
import org.refutix.refutix.web.server.util.ObjectMapperUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests for {@link MetadataController}. */
@SpringBootTest
@AutoConfigureMockMvc
public class MetadataControllerTest extends ControllerTestBase {

    private static final String METADATA_PATH = "/api/metadata/query";

    private static final String tablePath = "/api/table";

    private static final String catalogName = "paimon_catalog";

    private static final Integer catalogId = 1;

    private static final String databaseName = "paimon_database";

    private static final String tableName = "paimon_table";

    @Test
    public void testGetSchemaInfo() throws Exception {
        MetadataDTO metadata = new MetadataDTO();
        metadata.setCatalogId(catalogId);
        metadata.setDatabaseName(databaseName);
        metadata.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/schema")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(metadata))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> result = ObjectMapperUtils.fromJSON(response, new TypeReference<R<Void>>() {});
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetManifestInfo() throws Exception {
        MetadataDTO metadata = new MetadataDTO();
        metadata.setCatalogId(catalogId);
        metadata.setDatabaseName(databaseName);
        metadata.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/manifest")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(metadata))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> result = ObjectMapperUtils.fromJSON(response, new TypeReference<R<Void>>() {});
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetDataFileInfo() throws Exception {
        MetadataDTO metadata = new MetadataDTO();
        metadata.setCatalogId(catalogId);
        metadata.setDatabaseName(databaseName);
        metadata.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/dataFile")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(metadata))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> result = ObjectMapperUtils.fromJSON(response, new TypeReference<R<Void>>() {});
        assertEquals(200, result.getCode());
    }

    @Test
    public void testGetSnapshotInfo() throws Exception {
        MetadataDTO metadata = new MetadataDTO();
        metadata.setCatalogId(catalogId);
        metadata.setDatabaseName(databaseName);
        metadata.setTableName(tableName);

        String response =
                mockMvc.perform(
                                MockMvcRequestBuilders.post(METADATA_PATH + "/snapshot")
                                        .cookie(cookie)
                                        .content(ObjectMapperUtils.toJSON(metadata))
                                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                                        .accept(MediaType.APPLICATION_JSON_VALUE))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andDo(MockMvcResultHandlers.print())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        R<Void> result = ObjectMapperUtils.fromJSON(response, new TypeReference<R<Void>>() {});
        assertEquals(200, result.getCode());
    }
}
