package com.gianteyes.gaarigar.notification;

import com.gianteyes.gaarigar.user.UserModel;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class Note {
    private String subject;
    private String content;
    private String token;
    private HashMap<String, String> data;
    private UserModel user;
    private String templateName;
    private String mailSubject;
}
