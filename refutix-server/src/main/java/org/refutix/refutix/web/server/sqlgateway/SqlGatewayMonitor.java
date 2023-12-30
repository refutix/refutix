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

import org.apache.flink.table.gateway.SqlGateway;
import org.apache.flink.table.gateway.api.operation.OperationHandle;
import org.apache.flink.table.gateway.api.results.ResultSet;
import org.apache.flink.table.gateway.api.session.SessionHandle;
import org.apache.flink.table.gateway.service.operation.OperationManager;
import org.apache.flink.table.gateway.service.session.Session;
import org.apache.flink.table.gateway.service.session.SessionManagerImpl;
import org.joor.Reflect;
import org.refutix.refutix.web.common.sqlgateway.SqlGatewayOperation;
import org.refutix.refutix.web.common.sqlgateway.SqlGatewaySession;
import org.refutix.refutix.web.server.sqlgateway.event.SqlGatewayEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/** Sql gateway monitor. */
public class SqlGatewayMonitor {

    private static final String FILED_SESSION_MANAGER = "sessionManager";
    private static final String FILED_SESSIONS = "sessions";
    private static final String FILED_SUBMITTED_OPERATIONS = "submittedOperations";

    private final SqlGatewayProperties sqlGatewayProperties;
    private final ApplicationEventPublisher publisher;
    private final SqlGatewayChangeComputer changeComputer;

    public SqlGatewayMonitor(
            SqlGatewayProperties sqlGatewayProperties, ApplicationEventPublisher publisher) {
        this.sqlGatewayProperties = sqlGatewayProperties;
        this.publisher = publisher;
        this.changeComputer = new SqlGatewayChangeComputer();
    }

    public void monitor(SqlGateway sqlGateway) {
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(
                        () -> {
                            SessionManagerImpl sessionManager =
                                    Reflect.on(sqlGateway).get(FILED_SESSION_MANAGER);
                            Map<SessionHandle, Session> sessions =
                                    Reflect.on(sessionManager).get(FILED_SESSIONS);
                            publisher.publishEvent(
                                    new SqlGatewayEvent(
                                            changeComputer.compute(collectOperations(sessions)),
                                            this));
                        },
                        sqlGatewayProperties.getMonitor().getDelay(),
                        sqlGatewayProperties.getMonitor().getPeriod(),
                        sqlGatewayProperties.getMonitor().getTimeUnit());
    }

    private Map<SqlGatewaySession, List<SqlGatewayOperation>> collectOperations(
            Map<SessionHandle, Session> sessions) {
        return sessions.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                entry ->
                                        SqlGatewaySession.builder()
                                                .id(entry.getKey().getIdentifier())
                                                .build(),
                                entry -> {
                                    Map<OperationHandle, OperationManager.Operation> operations =
                                            Reflect.on(entry.getValue().getOperationManager())
                                                    .get(FILED_SUBMITTED_OPERATIONS);
                                    return operations.entrySet().stream()
                                            .map(
                                                    op -> {
                                                        SqlGatewayOperation operation =
                                                                SqlGatewayOperation.builder()
                                                                        .id(
                                                                                op.getKey()
                                                                                        .getIdentifier())
                                                                        .status(
                                                                                op.getValue()
                                                                                        .getOperationInfo()
                                                                                        .getStatus()
                                                                                        .name())
                                                                        .build();
                                                        try {
                                                            ResultSet resultSet =
                                                                    op.getValue()
                                                                            .fetchResults(
                                                                                    0,
                                                                                    Integer
                                                                                            .MAX_VALUE);
                                                            Optional.ofNullable(
                                                                            resultSet.getJobID())
                                                                    .ifPresent(
                                                                            jobID ->
                                                                                    operation
                                                                                            .setJobId(
                                                                                                    jobID
                                                                                                            .toString()));
                                                        } catch (Exception ignore) {
                                                        }
                                                        op.getValue()
                                                                .getOperationInfo()
                                                                .getException()
                                                                .ifPresent(
                                                                        e ->
                                                                                operation.setError(
                                                                                        e
                                                                                                        .getMessage()
                                                                                                + " cause by:"
                                                                                                + e.getCause()
                                                                                                        .getMessage()));
                                                        return operation;
                                                    })
                                            .collect(Collectors.toList());
                                }));
    }
}
