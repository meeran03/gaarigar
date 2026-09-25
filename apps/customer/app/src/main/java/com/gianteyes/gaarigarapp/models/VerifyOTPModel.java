package com.gianteyes.gaarigarapp.models;

import lombok.Data;

@Data
public class VerifyOTPModel {
    private String phoneNumber;
    private String OTPMessage;
}
