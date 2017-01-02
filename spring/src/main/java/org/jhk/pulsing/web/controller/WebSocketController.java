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
package org.jhk.pulsing.web.controller;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.jhk.pulsing.shared.util.RedisConstants;
import org.jhk.pulsing.web.pojo.light.Chat;
import org.jhk.pulsing.web.pojo.light.MapPulseCreate;
import org.jhk.pulsing.web.pojo.light.UserLight;
import org.jhk.pulsing.web.service.IChatService;
import org.jhk.pulsing.web.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * @author Ji Kim
 */
@Controller
public class WebSocketController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(WebSocketController.class);
    
    private static final int CHAT_LOBBY_INVITE_EXPIRATION = 300; // 5 minutes
    
    @Inject
    private IUserService userService;
    
    @Inject
    private IChatService chatService;
    
    @SendTo("/topics/pulseCreated")
    public MapPulseCreate pulseCreated(MapPulseCreate mPulseCreate) {
        _LOGGER.debug("WebSocketController.pulseCreated: " + mPulseCreate);
        
        return mPulseCreate;
    }
    
    @MessageMapping("/privateChat/{toUserId}")
    @SendTo("/topics/privateChat/{toUserId}")
    public Chat privateChat(@DestinationVariable long toUserId, @Payload Chat msg) {
        _LOGGER.debug("WebSocketController.privateChat: " + toUserId + "-" + msg);
        
        msg.setTimeStamp(Instant.now().toEpochMilli());
        
        if(msg.getType() == Chat.TYPE.CHAT_LOBBY_INVITE) {
            String invitationId = userService.createInvitationId(toUserId, RedisConstants.INVITATION_ID.CHAT_LOBBY_INVITE_, CHAT_LOBBY_INVITE_EXPIRATION);
            msg.addData("invitationId", invitationId);
        }
        
        return msg;
    }
    
    @MessageMapping("/chat/{chatId}")
    @SendTo("/topics/chat/{chatId}")
    public Chat chat(@DestinationVariable String chatId, @Payload Chat msg) {
        _LOGGER.debug("WebSocketController.chat: " + chatId + ": " + msg);
        
        Optional<UserLight> oUserLight = userService.getUserLight(msg.getUserId());
        oUserLight.ifPresent(user -> {
            msg.setPicturePath(user.getPicturePath());
        });
        
        msg.setTimeStamp(Instant.now().toEpochMilli());
        
        if(msg.getType() == Chat.TYPE.CHAT_LOBBY) {
            chatService.chatLobbyMessageInsert(UUID.fromString(chatId), msg.getUserId(), msg.getTimeStamp(), msg.getMessage());
        }
        
        return msg;
    }
    
}