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

package org.refutix.refutix.server.configrue;

import org.apache.flink.configuration.ConfigurationUtils;
import org.apache.flink.table.gateway.SqlGateway;
import org.apache.flink.table.gateway.service.context.DefaultContext;
import org.apache.flink.table.gateway.service.session.SessionManager;
import org.refutix.refutix.web.server.sqlgateway.SqlGatewayMonitor;
import org.refutix.refutix.web.server.sqlgateway.SqlGatewayProperties;
import org.refutix.refutix.web.server.sqlgateway.listener.SqlGatewayEventListener;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/** Sql gateway auto config. */
@Configuration
@EnableConfigurationProperties(SqlGatewayProperties.class)
public class SqlGatewayAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public SqlGateway sqlGateway(SqlGatewayProperties sqlGatewayProperties) {
        DefaultContext context =
                DefaultContext.load(
                        ConfigurationUtils.createConfiguration(
                                sqlGatewayProperties.dynamicConfig()),
                        List.of(),
                        true,
                        false);
        return new SqlGateway(context.getFlinkConfig(), SessionManager.create(context));
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlGatewayMonitor sqlGatewayMonitor(
            SqlGatewayProperties sqlGatewayProperties, ApplicationEventPublisher publisher) {
        return new SqlGatewayMonitor(sqlGatewayProperties, publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlGatewayEventListener sqlGatewayEventListener() {
        return event -> {
            if (!event.getChanged().isEmpty()) {
                LoggerFactory.getLogger("sql-gateway-logger")
                        .info("consume flink sql gateway change event:[{}]", event.getChanged());
            }
        };
    }
}
