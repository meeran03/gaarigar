package com.gianteyes.gaarigar.chat;

import lombok.Builder;

@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class TextMessage implements ChatMessage {
    private String channelId;
    private String sender;
    private String content;
    private MessageType type;

    @Override
    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @Override
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public void save() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
}
