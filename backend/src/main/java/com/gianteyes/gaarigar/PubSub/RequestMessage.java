package com.gianteyes.gaarigar.PubSub;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

@Data
@Builder
public class RequestMessage {
    String sender;
    HashMap<String, String> data;
}
