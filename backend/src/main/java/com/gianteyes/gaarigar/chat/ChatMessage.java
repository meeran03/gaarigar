package com.gianteyes.gaarigar.chat;

public interface ChatMessage {
    String getChannelId();

    String getSender();

    void save();
}
