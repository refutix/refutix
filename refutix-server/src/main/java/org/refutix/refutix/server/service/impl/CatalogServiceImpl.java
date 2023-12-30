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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.refutix.refutix.api.catalog.PaimonServiceFactory;
import org.refutix.refutix.server.data.dto.CatalogDTO;
import org.refutix.refutix.server.data.enums.CatalogMode;
import org.refutix.refutix.server.data.model.CatalogInfo;
import org.refutix.refutix.server.data.result.R;
import org.refutix.refutix.server.data.result.enums.Status;
import org.refutix.refutix.server.mapper.CatalogMapper;
import org.refutix.refutix.server.service.CatalogService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/** CatalogServiceImpl. */
@Service
public class CatalogServiceImpl extends ServiceImpl<CatalogMapper, CatalogInfo>
        implements CatalogService {
    @Override
    public boolean checkCatalogNameUnique(CatalogDTO catalogDTO) {
        CatalogInfo info =
                this.lambdaQuery().eq(CatalogInfo::getCatalogName, catalogDTO.getName()).one();
        return Objects.nonNull(info);
    }

    @Override
    public R<Void> createCatalog(CatalogDTO catalogDTO) {
        if (checkCatalogNameUnique(catalogDTO)) {
            return R.failed(Status.CATALOG_NAME_IS_EXIST, catalogDTO.getName());
        }

        if (catalogDTO.getType().equalsIgnoreCase(CatalogMode.FILESYSTEM.getMode())) {
            PaimonServiceFactory.createFileSystemCatalogService(
                    catalogDTO.getName(), catalogDTO.getWarehouse(), catalogDTO.getOptions());
        } else if (catalogDTO.getType().equalsIgnoreCase(CatalogMode.HIVE.getMode())) {
            if (StringUtils.isNotBlank(catalogDTO.getHiveConfDir())) {
                PaimonServiceFactory.createHiveCatalogService(
                        catalogDTO.getName(),
                        catalogDTO.getWarehouse(),
                        catalogDTO.getHiveUri(),
                        catalogDTO.getHiveConfDir());
            } else {
                PaimonServiceFactory.createHiveCatalogService(
                        catalogDTO.getName(),
                        catalogDTO.getWarehouse(),
                        catalogDTO.getHiveUri(),
                        null);
            }
        }

        CatalogInfo catalog =
                CatalogInfo.builder()
                        .catalogName(catalogDTO.getName())
                        .catalogType(catalogDTO.getType())
                        .warehouse(catalogDTO.getWarehouse())
                        .options(catalogDTO.getOptions())
                        .isDelete(false)
                        .build();

        return this.save(catalog) ? R.succeed() : R.failed();
    }
}
