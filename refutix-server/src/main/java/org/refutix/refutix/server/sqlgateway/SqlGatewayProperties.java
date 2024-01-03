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

package org.refutix.refutix.server.sqlgateway;

import lombok.Data;
import org.apache.flink.table.gateway.api.config.SqlGatewayServiceConfigOptions;
import org.refutix.refutix.server.constant.SqlGatewayConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.apache.flink.util.TimeUtils.formatWithHighestUnit;

/** Sql gateway properties. */
@Data
@ConfigurationProperties(prefix = "sql-gateway")
public class SqlGatewayProperties {

    private Session session = new Session();
    private Worker worker = new Worker();
    private Endpoint endpoint = new Endpoint();

    private Monitor monitor = new Monitor();

    /** Session. */
    @Data
    public static class Session {

        private Duration checkInterval =
                Duration.of(SqlGatewayConstant.SESSION_CHECK_INTERVAL, ChronoUnit.MINUTES);
        private Duration idleTimeout =
                Duration.of(SqlGatewayConstant.SESSION_IDLE_TIMEOUT, ChronoUnit.MINUTES);
        private int maxNum = SqlGatewayConstant.SESSION_MAX_NUM;
    }

    /** Worker. */
    @Data
    public static class Worker {

        private Duration keepaliveTime =
                Duration.of(SqlGatewayConstant.WORKER_KEEPALIVE_TIME, ChronoUnit.MINUTES);
        private Threads threads = new Threads();
    }

    /** Threads. */
    @Data
    public static class Threads {

        private int max = SqlGatewayConstant.MAX_THREADS;
        private int min = SqlGatewayConstant.MIN_THREADS;
    }

    /** Endpoint. */
    @Data
    public static class Endpoint {

        private Rest rest = new Rest();
    }

    /** Rest. */
    @Data
    public static class Rest {
        private String address = SqlGatewayConstant.ADDRESS;
        private String bindAddress;
        private int port = SqlGatewayConstant.PORT;
        private String bindPort = SqlGatewayConstant.BIND_PORT;
    }

    /** Monitor. */
    @Data
    public static class Monitor {

        private int delay = SqlGatewayConstant.MONITOR_DELAY;
        private int period = SqlGatewayConstant.MONITOR_PERIOD;
        private TimeUnit timeUnit = TimeUnit.SECONDS;
    }

    public Properties dynamicConfig() {
        Properties dynamicConfig = new Properties();
        // rest
        dynamicConfig.setProperty(
                SqlGatewayConstant.SQL_GATEWAY_ENDPOINT_REST_ADDRESS,
                this.getEndpoint().getRest().getAddress());
        dynamicConfig.setProperty(
                SqlGatewayConstant.SQL_GATEWAY_ENDPOINT_REST_PORT,
                String.valueOf(this.getEndpoint().getRest().getPort()));
        dynamicConfig.setProperty(
                SqlGatewayConstant.SQL_GATEWAY_ENDPOINT_REST_BIND_PORT,
                this.getEndpoint().getRest().getBindPort());
        if (Objects.nonNull(this.getEndpoint().getRest().getBindAddress())) {
            dynamicConfig.setProperty(
                    SqlGatewayConstant.SQL_GATEWAY_ENDPOINT_REST_BIND_ADDRESS,
                    this.getEndpoint().getRest().getBindAddress());
        }

        // session
        dynamicConfig.setProperty(
                SqlGatewayServiceConfigOptions.SQL_GATEWAY_SESSION_CHECK_INTERVAL.key(),
                formatWithHighestUnit(this.getSession().getCheckInterval()));
        dynamicConfig.setProperty(
                SqlGatewayServiceConfigOptions.SQL_GATEWAY_SESSION_IDLE_TIMEOUT.key(),
                formatWithHighestUnit(this.getSession().getIdleTimeout()));
        dynamicConfig.setProperty(
                SqlGatewayServiceConfigOptions.SQL_GATEWAY_SESSION_MAX_NUM.key(),
                String.valueOf(this.getSession().getMaxNum()));

        // worker
        dynamicConfig.setProperty(
                SqlGatewayServiceConfigOptions.SQL_GATEWAY_WORKER_KEEPALIVE_TIME.key(),
                formatWithHighestUnit(this.getWorker().getKeepaliveTime()));
        dynamicConfig.setProperty(
                SqlGatewayServiceConfigOptions.SQL_GATEWAY_WORKER_THREADS_MAX.key(),
                String.valueOf(this.getWorker().getThreads().getMax()));
        dynamicConfig.setProperty(
                SqlGatewayServiceConfigOptions.SQL_GATEWAY_WORKER_THREADS_MIN.key(),
                String.valueOf(this.getWorker().getThreads().getMin()));
        return dynamicConfig;
    }
}
