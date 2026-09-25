package com.gianteyes.gaarigar.OTP;


import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("twilio")
public class TwilioSMSSender {
    @org.springframework.beans.factory.annotation.Value("${gaarigar.integrations.enabled:false}")
    private boolean integrationsEnabled;

    @Autowired
    private TwilioConfig twilioConfig;

    @Value("${twilioPhoneNumber}")
    private String twilioNumber;
    public void sendSms(OTPModel otpModel,String message) {
        if (!integrationsEnabled) throw new IllegalStateException("SMS is not configured");

        PhoneNumber to=new PhoneNumber(otpModel.getPhoneNumber());
        PhoneNumber from=new PhoneNumber(this.twilioNumber);
        String msg=message +  otpModel.getOTPMessage();
        MessageCreator sendMsg= Message.creator(to,from,msg);
        sendMsg.create();
    }

}
