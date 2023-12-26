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

package org.refutix.refutix.web.api.table.metadata;

import org.apache.paimon.types.DataType;

import javax.annotation.Nullable;

/** table metadata. */
public class ColumnMetadata {

    private final String name;

    private final DataType type;

    private final @Nullable String description;

    public ColumnMetadata(String name) {
        this(name, null, null);
    }

    public ColumnMetadata(String name, DataType type) {
        this(name, type, null);
    }

    public ColumnMetadata(String name, DataType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public String name() {
        return this.name;
    }

    public DataType type() {
        return this.type;
    }

    public String description() {
        return this.description;
    }

    public ColumnMetadata copy(DataType newDataType) {
        return new ColumnMetadata(this.name, newDataType, this.description);
    }

    public ColumnMetadata setComment(String comment) {
        return comment == null ? this : new ColumnMetadata(this.name, this.type, comment);
    }
}
