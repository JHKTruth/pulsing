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
package org.jhk.pulsing.cascading.cascalog.function;

import org.jhk.pulsing.serialization.thrift.data.Data;
import org.jhk.pulsing.serialization.thrift.data.DataUnit;
import org.jhk.pulsing.serialization.thrift.edges.TagGroupUserEdge;
import org.jhk.pulsing.serialization.thrift.id.TagGroupId;
import org.jhk.pulsing.serialization.thrift.property.TagGroupProperty;
import org.jhk.pulsing.serialization.thrift.property.TagGroupPropertyValue;
import org.jhk.pulsing.serialization.thrift.property.UserProperty;
import org.jhk.pulsing.serialization.thrift.property.UserPropertyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cascading.flow.FlowProcess;
import cascading.operation.FunctionCall;
import cascading.tuple.Tuple;
import cascalog.CascalogFunction;

/**
 * @author Ji Kim
 */
public final class EmitDataUnitFieldFunction extends CascalogFunction {
    
    private static final long serialVersionUID = -4957675602128406445L;
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(CascalogFunction.class);
    
    public enum EMIT_DATA_UNIT_FIELD {
        USER_PROPERTY {
            @Override
            Tuple emitDataField(DataUnit dUnit) {
                UserProperty uProperty = dUnit.getUser_property();
                UserPropertyValue upValue = uProperty.getProperty();
                
                return new Tuple(uProperty.getId().getId(), upValue.getName(), upValue.getEmail(),
                        upValue.getLat(), upValue.getLng());
            }
        },
        
        TAG_GROUP_USER_EDGE {
            @Override
            Tuple emitDataField(DataUnit dUnit) {
                TagGroupUserEdge tguEdge = dUnit.getTaggroupuser_edge();
                TagGroupId tgId = tguEdge.getTagGroupId();
                
                return new Tuple(tgId.getId(), tgId.getLat(), tgId.getLng(), tguEdge.getUserId().getId());
            }
        },
        
        TAG_GROUP_PROPERTY {
            @Override
            Tuple emitDataField(DataUnit dUnit) {
                TagGroupProperty tgProperty = dUnit.getTaggroup_property();
                TagGroupPropertyValue tgpValue = tgProperty.getProperty();
                TagGroupId tgId = tgProperty.getId();
                
                return new Tuple(tgId.getId(), tgId.getLat(), tgId.getLng(), tgpValue.getTag());
            }
        };
        
        abstract Tuple emitDataField(DataUnit dUnit);
    }
    
    private EMIT_DATA_UNIT_FIELD _eDUnitField;
    
    public EmitDataUnitFieldFunction(EMIT_DATA_UNIT_FIELD eDUnitField) {
        super();
        
        _eDUnitField = eDUnitField;
    }

    @Override
    public void operate(FlowProcess fProcess, FunctionCall fCall) {
        
        Data data = (Data) fCall.getArguments().getObject(0);
        Tuple tuple = _eDUnitField.emitDataField(data.getDataunit());
        
        _LOGGER.info("EmitDataUnitFieldFunction.operate: Emitting " + tuple);
        fCall.getOutputCollector().add(tuple);
    }

}
