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
package org.jhk.pulsing.shared.util;

/**
 * @author Ji Kim
 */
public final class PulsingConstants {
    
    public static final int DEFAULT_INTERVAL_SECONDS = 15;
    
    public static final String DEFAULT_BOOTSTRAP_HOST = "0.0.0.0";
    public static final int DEFAULT_BOOTSTRAP_PORT = 9092;
    
    public static final String REDIS_HOST = "localhost";
    public static final int REDIS_PORT = 6379;
    
    public static final int HASH_CODE_INIT_VALUE = 3;
    public static final int HASH_CODE_MULTIPLY_VALUE = 31;
    
    public enum TOPICS {
		PULSE_SUBSCRIBE;
	};
    
    private PulsingConstants() {
        super();
    }
    
}
