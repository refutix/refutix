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

package org.refutix.refutix.web.server.util;

import org.apache.commons.lang3.StringUtils;
import org.refutix.refutix.web.api.catalog.PaimonService;
import org.refutix.refutix.web.api.catalog.PaimonServiceFactory;
import org.refutix.refutix.web.server.data.enums.CatalogMode;
import org.refutix.refutix.web.server.data.model.CatalogInfo;

/** Paimon Service util. */
public class PaimonServiceUtils {

    /**
     * Get a Paimon Service based on the provided CatalogInfo.
     *
     * @param catalogInfo The CatalogInfo object containing the catalog details.
     * @return The created PaimonService object.
     */
    public static PaimonService getPaimonService(CatalogInfo catalogInfo) {
        PaimonService service;
        if (catalogInfo.getCatalogType().equalsIgnoreCase(CatalogMode.FILESYSTEM.getMode())) {
            service =
                    PaimonServiceFactory.createFileSystemCatalogService(
                            catalogInfo.getCatalogName(),
                            catalogInfo.getWarehouse(),
                            catalogInfo.getOptions());
        } else if (catalogInfo.getCatalogType().equalsIgnoreCase(CatalogMode.HIVE.getMode())) {
            if (StringUtils.isNotBlank(catalogInfo.getHiveConfDir())) {
                service =
                        PaimonServiceFactory.createHiveCatalogService(
                                catalogInfo.getCatalogName(),
                                catalogInfo.getWarehouse(),
                                catalogInfo.getHiveUri(),
                                catalogInfo.getHiveConfDir());
            } else {
                service =
                        PaimonServiceFactory.createHiveCatalogService(
                                catalogInfo.getCatalogName(),
                                catalogInfo.getWarehouse(),
                                catalogInfo.getHiveUri(),
                                null);
            }
        } else {
            service =
                    PaimonServiceFactory.createFileSystemCatalogService(
                            catalogInfo.getCatalogName(),
                            catalogInfo.getWarehouse(),
                            catalogInfo.getOptions());
        }
        return service;
    }
}
