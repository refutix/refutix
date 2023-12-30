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

package org.refutix.refutix.web.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.table.gateway.SqlGateway;
import org.refutix.refutix.web.server.sqlgateway.SqlGatewayMonitor;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/** Refutix Server Application. */
@Slf4j
@SpringBootApplication
public class RefutixServerApplication {

    /**
     * Main.
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(RefutixServerApplication.class, args);
    }

    /** Flink sql gateway startup class. */
    @Component
    public static class FlinkSqlGatewayStartup
            implements CommandLineRunner, ApplicationContextAware {

        private final SqlGateway sqlGateway;
        private final SqlGatewayMonitor sqlGatewayMonitor;

        private ApplicationContext ctx;

        public FlinkSqlGatewayStartup(SqlGateway sqlGateway, SqlGatewayMonitor sqlGatewayMonitor) {
            this.sqlGateway = sqlGateway;
            this.sqlGatewayMonitor = sqlGatewayMonitor;
        }

        @Override
        public void run(String... args) {
            try {
                sqlGateway.start();
                sqlGatewayMonitor.monitor(sqlGateway);
                log.info("start Flink SQL Gateway embedded server.");
            } catch (Throwable ex) {
                log.error("start Flink SQL Gateway embedded server failed:", ex);
                ((ConfigurableApplicationContext) ctx).close();
            }
        }

        @Override
        public void setApplicationContext(ApplicationContext ctx) throws BeansException {
            this.ctx = ctx;
        }
    }
}
