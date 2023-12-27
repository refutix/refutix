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

package org.refutix.refutix.plugin.flink.operator.session;

import com.google.auto.service.AutoService;
import org.refutix.refutix.plugin.flink.session.FlinkSessionService;
import org.refutix.refutix.plugin.flink.session.FlinkSessionServiceFactory;

import java.util.Properties;

/** FlinkKubernetesOperatorSessionServiceFactory {@link FlinkSessionServiceFactory} */
@AutoService(FlinkSessionServiceFactory.class)
public class FlinkKubernetesOperatorSessionServiceFactory implements FlinkSessionServiceFactory {
    @Override
    public boolean flinkVersionMatch(String flinkVersion) {
        return flinkVersion.startsWith("1.15")
                || flinkVersion.startsWith("1.16")
                || flinkVersion.startsWith("1.17")
                || flinkVersion.startsWith("1.18");
    }

    @Override
    public String name() {
        return "flink-kubernetes-operator";
    }

    @Override
    public FlinkSessionService create(Properties properties) {
        return new FlinkKubernetesOperatorSessionService();
    }
}
