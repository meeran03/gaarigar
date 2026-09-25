package com.gianteyes.gaarigarmechanic.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatMessage {
    private String channelId;
    private String sender;
    private String content;
    private MessageType type;
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
