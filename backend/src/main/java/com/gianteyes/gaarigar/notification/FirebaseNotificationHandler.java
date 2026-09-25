package com.gianteyes.gaarigar.notification;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
public class FirebaseNotificationHandler implements NotificationHandler {

//    @Autowired
//    private FirebaseMessaging firebaseMessaging;


    public String send(Note note) throws FirebaseMessagingException, ExecutionException, InterruptedException {

        Notification notification = Notification
                .builder()
                .setTitle(note.getSubject())
                .setBody(note.getContent())
                .build();

        if (note.getData() == null) {
            note.setData(new HashMap<>());
        }

        Message message = Message
                .builder()
                .setToken(note.getToken())
                .setNotification(notification)
                .putAllData(note.getData())
                .build();

//        return firebaseMessaging.sendAsync(message).get();
        // we use server_token legacy api for sending notification, by using http
        // request to firebase server
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://fcm.googleapis.com/fcm/send";
        String serverKey = System.getenv("FCM_SERVER_KEY");
        if (serverKey == null || serverKey.isBlank()) throw new IllegalStateException("FCM is not configured");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "key=" + serverKey);

        HashMap<String, Object> body = new HashMap<>();
        body.put("to", note.getToken());
        HashMap<String, Object> notificationMap = new HashMap<>();
        notificationMap.put("title", note.getSubject());
        notificationMap.put("body", note.getContent());
        body.put("notification", notificationMap);
        body.put("data", note.getData());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        return response.getBody();
    }

}
