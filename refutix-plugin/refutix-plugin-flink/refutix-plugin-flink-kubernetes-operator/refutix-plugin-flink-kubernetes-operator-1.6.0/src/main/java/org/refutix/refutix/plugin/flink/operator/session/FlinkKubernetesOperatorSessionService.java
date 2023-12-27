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

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.apache.flink.kubernetes.operator.api.FlinkDeployment;
import org.refutix.refutix.plugin.flink.session.FlinkSessionService;

/** FlinkSessionService impl by flink k8s operator {@link FlinkSessionService}. */
public class FlinkKubernetesOperatorSessionService implements FlinkSessionService {

    private final KubernetesClient kubernetesClient;

    public FlinkKubernetesOperatorSessionService() {
        // TODO kubernetes client build
        kubernetesClient = new KubernetesClientBuilder().build();
    }

    @Override
    public void createSession() {
        FlinkDeployment flinkDeployment = new FlinkDeployment();
        // TODO flink deployment build
    }

    @Override
    public void deleteSession() {}

    @Override
    public void submitJob() {}

    @Override
    public void stopJob() {}
}
