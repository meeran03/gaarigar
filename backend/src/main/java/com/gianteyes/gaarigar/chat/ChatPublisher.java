package com.gianteyes.gaarigar.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import static java.lang.String.format;

@Controller
public class ChatPublisher {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/{channelId}/send")
    public void send(@DestinationVariable String channelId, @Payload TextMessage message, java.security.Principal principal) {
        message.setSender(principal.getName());
        message.setChannelId(channelId);
        messagingTemplate.convertAndSend(format("/channel/%s", channelId), message);
    }

    @MessageMapping("/chat/{channelId}/addSubscriber")
    public void addSubscriber(@DestinationVariable String channelId, @Payload TextMessage message,
                              SimpMessageHeaderAccessor headerAccessor, java.security.Principal principal) {
        message.setSender(principal.getName());
        message.setChannelId(channelId);
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put("channelId", channelId);
        if (currentRoomId != null) {
            System.out.println("leave");
        }
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        messagingTemplate.convertAndSend(format("/channel/%s", channelId), message);
    }
}
