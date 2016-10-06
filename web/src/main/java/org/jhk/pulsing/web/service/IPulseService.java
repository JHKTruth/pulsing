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
package org.jhk.pulsing.web.service;

import java.util.Map;
import java.util.Set;

import org.jhk.pulsing.serialization.avro.records.Pulse;
import org.jhk.pulsing.serialization.avro.records.PulseId;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import org.jhk.pulsing.web.pojo.light.UserLight;

/**
 * @author Ji Kim
 */
public interface IPulseService {
    
    Result<Pulse> getPulse(PulseId pulseId);
    
    Result<Pulse> createPulse(Pulse pulse);
    
    Result<PulseId> subscribePulse(Pulse pulse, UserId userId);
    
    Result<String> unSubscribePulse(Pulse pulse, UserId userId);
    
    Map<Long, String> getTrendingPulseSubscriptions(int numMinutes);
    
    Map<Pulse, Set<UserLight>> getMapPulseDataPoints(Double lat, Double lng);
    
}
