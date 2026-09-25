package com.gianteyes.gaarigar.OTP;
import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="gaarigar.integrations.enabled", havingValue="true")
@Configuration
public class TwilioInitializer {
   // private final TwilioConfig twilioConfig;
    @Value("${accountSid}")
    private String accountSid;

    @Value("${twilioAuthToken}")
    private String authToken;

    @Autowired
    public TwilioInitializer( @Value("${accountSid}")String accountSid,@Value("${twilioAuthToken}")String auth) {
       // this.twilioConfig = twilioConfig;
        Twilio.init(accountSid,auth);

    }
}
