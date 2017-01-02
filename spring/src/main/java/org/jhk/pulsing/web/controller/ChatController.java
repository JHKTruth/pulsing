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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.web.common.Result;
import static org.jhk.pulsing.web.common.Result.CODE.*;
import org.jhk.pulsing.web.pojo.light.Chat;
import org.jhk.pulsing.web.pojo.light.UserLight;
import org.jhk.pulsing.web.service.IChatService;
import org.jhk.pulsing.web.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Ji Kim
 */
@CrossOrigin(origins="*")
@Controller
@RequestMapping("/chat")
public class ChatController {
    
    private static final Logger _LOGGER = LoggerFactory.getLogger(ChatController.class);
    
    private ObjectMapper _objectMapper = new ObjectMapper();
    
    @Inject
    private IUserService userService;
    
    @Inject
    private IChatService chatService;
    
    @Inject
    private SimpMessagingTemplate template;
    
    @RequestMapping(value="/queryChatLobbies", method=RequestMethod.GET)
    public @ResponseBody Result<Map<String, UUID>> queryChatLobby(@RequestParam UserId userId) {
        _LOGGER.debug("ChatController.queryChatLobbies: " + userId);
        
        return chatService.queryChatLobbies(userId);
    }
    
    /**
     * @param cLId
     * @param timeStamp held as milliseconds in back end
     * @return
     */
    @RequestMapping(value="/queryChatLobbyMessages", method=RequestMethod.GET)
    public @ResponseBody Result<List<Chat>> queryChatLobbyMessages(@RequestParam UUID cLId, @RequestParam Long timeStamp) {
        _LOGGER.debug("ChatController.queryChatLobbyMessages: " + cLId + " - " + timeStamp);
        
        return chatService.queryChatLobbyMessages(cLId, timeStamp);
    }
    
    @RequestMapping(value="/createChatLobby", method=RequestMethod.POST)
    public @ResponseBody Result<UUID> createChatLobby(UserId userId, String lobbyName) {
        _LOGGER.debug("ChatController.createChatLobby: " + userId + " - " + lobbyName);
        
        return chatService.createChatLobby(userId, lobbyName);
    }
    
    @RequestMapping(value="/chatLobbySubscribe/{cLId}/{lobbyName}/{chatLobbyInvitationId}/{userId}", method=RequestMethod.PUT)
    public @ResponseBody Result<Boolean> chatLobbySubscribe(@PathVariable UUID cLId, @PathVariable String lobbyName, 
                                                                @PathVariable String chatLobbyInvitationId, @PathVariable UserId userId) {
        _LOGGER.debug("ChatController.chatLobbySubscribe: " + cLId + " - " + lobbyName + " : " + userId + ";" + chatLobbyInvitationId);
        
        if(!userService.removeInvitationId(chatLobbyInvitationId)) {
            return new Result<>(FAILURE, null, "Failed to subscribe to chatLobby " + lobbyName + " - the invitationId has expired.");
        }
        
        Result<Boolean> chatSubscribe = chatService.chatLobbySubscribe(cLId, lobbyName, userId);
        
        if(chatSubscribe.getData()) {
            
            Optional<UserLight> uLight = userService.getUserLight(userId.getId());
            try {
                String sMessage = "User " + (uLight.isPresent() ? uLight.get().getName() + " " : "") + " joined the chat lobby " + lobbyName + ", welcome him/her!!!";
                
                template.convertAndSend("/topics/chat/" + cLId, _objectMapper.writeValueAsString(createSystemMessage(sMessage)));
            } catch (Exception except) {
                _LOGGER.error("Error while converting pulse ", except);
                except.printStackTrace();
            }
        }
        
        return chatSubscribe;
    }
    
    private Chat createSystemMessage(String message) {
        
        Chat msg = new Chat();
        msg.setMessage(message);
        msg.setName("System Notification");
        msg.setType(Chat.TYPE.SYSTEM_MESSAGE);
        msg.setUserId(IUserService.SYSTEM_USER_ID);
        
        return msg;
    }
    
}