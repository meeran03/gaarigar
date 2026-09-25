package com.gianteyes.gaarigar.notification;

import com.gianteyes.gaarigar.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class NotificationUtil {
    @org.springframework.beans.factory.annotation.Value("${gaarigar.integrations.enabled:false}")
    private boolean integrationsEnabled;

    @Autowired
    private FirebaseNotificationHandler firebaseNotificationHandler;
    @Autowired
    private EmailNotificationHandler emailNotificationHandler;

    public ArrayList<NotificationHandler> getNotificationHandlers(UserModel user) {
        ArrayList<NotificationHandler> notificationHandlers = new ArrayList<>();
        if (user.getPhone() != null) {
//            notificationHandlers.add(new SMSNotificationHandler());
        }
        if (user.getFcmToken() != null) {
            notificationHandlers.add(this.firebaseNotificationHandler);
        }
        if (user.getEmailNotifications() != null && user.getEmailNotifications()) {
            notificationHandlers.add(this.emailNotificationHandler);
        }
        return notificationHandlers;
    }

    public void sendNotification(Note note) {
        if (!integrationsEnabled) return;
        ArrayList<NotificationHandler> notificationHandlers = getNotificationHandlers(note.getUser());
        notificationHandlers.forEach(notificationHandler -> {
            try {
                notificationHandler.send(note);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
