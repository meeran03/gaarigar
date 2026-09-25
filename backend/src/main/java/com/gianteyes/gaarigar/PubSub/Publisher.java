package com.gianteyes.gaarigar.PubSub;

import com.gianteyes.gaarigar.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Publisher {
    @Autowired
    private SimpMessagingTemplate template;

    public void publish(RequestMessage message, List<? extends UserModel> subscribers, String topic) {
        for (UserModel subscriber : subscribers) {
            template.convertAndSendToUser(subscriber.getPhone(), topic, message);
        }
    }
}
