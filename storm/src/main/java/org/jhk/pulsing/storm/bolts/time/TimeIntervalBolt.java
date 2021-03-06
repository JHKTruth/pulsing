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
package org.jhk.pulsing.storm.bolts.time;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.jhk.pulsing.shared.util.CommonConstants;
import org.jhk.pulsing.shared.util.Util;
import static org.jhk.pulsing.storm.common.FieldConstants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ji Kim
 */
public final class TimeIntervalBolt extends BaseBasicBolt {
    
    private static final long serialVersionUID = 3963343874691297355L;
    private static final Logger _LOGGER = LoggerFactory.getLogger(TimeIntervalBolt.class);
    
    private int _secondsInterval;
    
    public TimeIntervalBolt() {
        this(CommonConstants.DEFAULT_STORM_INTERVAL_SECONDS);
    }
    
    public TimeIntervalBolt(int secondsInterval) {
        super();
        
        _secondsInterval = secondsInterval;
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector outputCollector) {
        _LOGGER.info("TimeIntervalBolt.execute: {}", tuple);
        
        Long timeStamp = tuple.getLongByField(TIMESTAMP);
        Long id = tuple.getLongByField(ID);
        long timeInterval = Util.getTimeInterval(timeStamp, _secondsInterval);
        Object value = tuple.getValueByField(VALUE);
        
        _LOGGER.info("TimeIntervalBolt.execute timeInterval: timestamp={}, secondsInterval={}, timeInterval={}",
                timeStamp, _secondsInterval, timeInterval);
        
        //technically should escape or convert to hex string to avoid, but ok now
        StringBuilder builder = new StringBuilder();
        builder.append(id);
        builder.append(CommonConstants.TIME_INTERVAL_ID_VALUE_DELIM);
        builder.append(value != null ? value : "");
        
        outputCollector.emit(new Values(timeInterval,
                                builder.toString()));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(new Fields(TIME_INTERVAL, TIME_INTERVAL_VALUE));
    }
    
}
