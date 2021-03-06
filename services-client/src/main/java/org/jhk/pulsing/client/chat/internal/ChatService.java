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
package org.jhk.pulsing.client.chat.internal;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.jhk.pulsing.client.AbstractService;
import org.jhk.pulsing.client.chat.IChatService;
import org.jhk.pulsing.client.payload.chat.Chat;
import org.jhk.pulsing.client.payload.chat.PagingResult;
import org.jhk.pulsing.search.elasticsearch.client.ESRestClient;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.AesCipher;
import org.jhk.pulsing.shared.util.CommonConstants.SERVICE_ENV_KEY;
import org.jhk.pulsing.shared.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jhk.pulsing.client.payload.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @author Ji Kim
 */
@Service
public class ChatService extends AbstractService
                            implements IChatService {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ChatService.class);
    
    private static final String SECRETS_INDEX = "secrets";
    private static final String SECRET_MESSAGE_TYPE = "message";
    
    private final AesCipher aCipher = new AesCipher();
    private final ESRestClient esRClient = new ESRestClient();
    
    @Autowired
    private Environment environment;
    
    @Override
    public Result<UUID> createChatLobby(UserId userId, String lobbyName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result<String> chatLobbyUnSubscribe(UserId userId, UUID cLId, String lobbyName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result<Map<String, UUID>> queryChatLobbies(UserId userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Result<PagingResult<List<Chat>>> queryChatLobbyMessages(UUID cLId, UserId userId,
            Optional<String> pagingState) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void chatLobbyMessageInsert(UUID cLId, UUID msgId, long from, long timeStamp, String message) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Result<Boolean> chatLobbySubscribe(UUID cLId, String lobbyName, UserId userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void sendSecretMessage(long toUserId, Chat message) {
        _LOGGER.info("sendSecretMessage toUserId {}, message {}.", toUserId, message);
        
        try {
            Map<String, String> sMessage = new HashMap<>();
            sMessage.put("to_user_id", toUserId + "");
            sMessage.put("timestamp", message.getTimeStamp() + "");
            sMessage.put("from_user_id", message.getUserId() + "");
            sMessage.put("content", aCipher.encrypt(message.getMessage()));
            
            esRClient.putDocument(SECRETS_INDEX, SECRET_MESSAGE_TYPE, Util.uniqueId(), sMessage);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException encryptException) {
            encryptException.printStackTrace();
            _LOGGER.error("ChatService.sendSecretMessage: encryption error");
        }
    }

    @Override
    public Optional<String> checkPaging(String paging) {
        return paging == null || paging.length() == 0 ? Optional.empty() : Optional.of(paging);
    }
    
    @Override
    public String getBaseUrl() {
        return environment.getProperty(SERVICE_ENV_KEY.CHAT_SERVICE_ENDPOINT.name()); 
    }

}
