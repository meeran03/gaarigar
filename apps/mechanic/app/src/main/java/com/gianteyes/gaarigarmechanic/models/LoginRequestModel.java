package com.gianteyes.gaarigarmechanic.models;

public class LoginRequestModel {
    private String phone;
    private String password;
    private String fcmToken;

    public LoginRequestModel(final String phone, final String password, final String fcmToken) {
        this.phone = phone;
        this.password = password;
        this.fcmToken = fcmToken;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getFcmToken() {
        return this.fcmToken;
    }

    public void setFcmToken(final String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
