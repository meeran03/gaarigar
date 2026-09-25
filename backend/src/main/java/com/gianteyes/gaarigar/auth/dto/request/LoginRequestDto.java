package com.gianteyes.gaarigar.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public class LoginRequestDto {
    public String fcmToken;
    @NotNull(message = "Phone number cannot be null")
    private String phone;
    @NotNull(message = "Password cannot be null")
    private String password;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
