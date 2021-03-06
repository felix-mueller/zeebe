/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeebe.client.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.zeebe.client.clustering.impl.ClientTopologyManager;
import io.zeebe.client.clustering.impl.TopologyImpl;

public class RoundRobinDispatchStrategy implements RequestDispatchStrategy
{

    protected final ClientTopologyManager topologyManager;
    protected Map<String, AtomicInteger> topicOffsets = new ConcurrentHashMap<>();

    public RoundRobinDispatchStrategy(ClientTopologyManager topologyManager)
    {
        this.topologyManager = topologyManager;
    }

    @Override
    public int determinePartition(String topic)
    {
        final TopologyImpl topology = topologyManager.getTopology();

        final AtomicInteger offsetCounter = topicOffsets.computeIfAbsent(topic, t -> new AtomicInteger(0));
        final int offset = offsetCounter.getAndIncrement();

        return topology.getPartition(topic, offset);
    }
}
