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

package org.refutix.refutix.web.server.sqlgateway;

import org.refutix.refutix.web.common.sqlgateway.SqlGatewayOperation;
import org.refutix.refutix.web.common.sqlgateway.SqlGatewaySession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Sql gateway change computer. */
public class SqlGatewayChangeComputer {

    private Map<SqlGatewaySession, List<SqlGatewayOperation>> before = new HashMap<>();

    public Map<SqlGatewaySession, List<SqlGatewayOperation>> compute(
            Map<SqlGatewaySession, List<SqlGatewayOperation>> current) {
        if (before.isEmpty()) {
            before = current;
            return before;
        }

        // first: add new sessions
        Map<SqlGatewaySession, List<SqlGatewayOperation>> changed =
                current.entrySet().stream()
                        .filter(entry -> !before.containsKey(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // second: add new operations
        Map<SqlGatewaySession, List<SqlGatewayOperation>> candidate =
                current.entrySet().stream()
                        .filter(
                                entry ->
                                        before.containsKey(entry.getKey())
                                                && !before.get(entry.getKey())
                                                        .equals(entry.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        changed.putAll(
                candidate.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        entry -> {
                                            entry.getValue().removeAll(before.get(entry.getKey()));
                                            return entry.getValue();
                                        })));

        before = current;
        return changed;
    }
}
