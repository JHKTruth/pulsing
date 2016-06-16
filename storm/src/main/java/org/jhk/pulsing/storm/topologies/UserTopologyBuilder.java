/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jhk.pulsing.storm.topologies;

import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.trident.TransactionalTridentKafkaSpout;
import org.apache.storm.kafka.trident.TridentKafkaConfig;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.tuple.Fields;
import org.jhk.pulsing.storm.deserializers.avro.UserDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class UserTopologyBuilder {
    
    private static final Logger _LOG = LoggerFactory.getLogger(UserTopologyBuilder.class);
    
    public static StormTopology build() {
        _LOG.debug("UserTopologyBuilder.build");
        
        TridentTopology topology = new TridentTopology();
        topology.newStream("user-submission-spout", buildSpout())
            .each(
                    new Fields("str"), 
                    new UserDeserializer(), 
                    UserDeserializer.FIELDS
                    );
        
        return topology.build();
    }
    
    private static TransactionalTridentKafkaSpout buildSpout() {
        BrokerHosts host = new ZkHosts("localhost");
        TridentKafkaConfig spoutConfig = new TridentKafkaConfig(host, "user-submission");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        return new TransactionalTridentKafkaSpout(spoutConfig);
    }

}
