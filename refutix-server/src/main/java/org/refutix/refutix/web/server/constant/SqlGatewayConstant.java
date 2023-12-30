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

package org.refutix.refutix.web.server.constant;

/** Sql gateway constant. */
public class SqlGatewayConstant {

    public static final String SQL_GATEWAY_ENDPOINT_REST_ADDRESS =
            "sql-gateway.endpoint.rest.address";
    public static final String SQL_GATEWAY_ENDPOINT_REST_PORT = "sql-gateway.endpoint.rest.port";
    public static final String SQL_GATEWAY_ENDPOINT_REST_BIND_PORT =
            "sql-gateway.endpoint.rest.bind-port";
    public static final String SQL_GATEWAY_ENDPOINT_REST_BIND_ADDRESS =
            "sql-gateway.endpoint.rest.bind-addresss";

    public static final String ADDRESS = "127.0.0.1";
    public static final int PORT = 8083;
    public static final String BIND_PORT = "8083";

    public static final long SESSION_CHECK_INTERVAL = 0;
    public static final long SESSION_IDLE_TIMEOUT = 0;
    public static final int SESSION_MAX_NUM = 1000000;

    public static final long WORKER_KEEPALIVE_TIME = 5;
    public static final int MAX_THREADS = 500;
    public static final int MIN_THREADS = 5;

    public static final int MONITOR_DELAY = 0;
    public static final int MONITOR_PERIOD = 10;
}
