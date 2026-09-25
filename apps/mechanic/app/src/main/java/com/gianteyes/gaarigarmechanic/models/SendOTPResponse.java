package com.gianteyes.gaarigarmechanic.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SendOTPResponse {
    @Expose
    @SerializedName("successMessage")
    private String successMessage;
    @Expose
    @SerializedName("expireDate")
    private String expireDate; // of the form 31-10-2022 18:56:00

    public SendOTPResponse(final String successMessage, final String expireDate) {
        this.successMessage = successMessage;
        this.expireDate = expireDate;
    }

    public String getSuccessMessage() {
        return this.successMessage;
    }

    public void setSuccessMessage(final String successMessage) {
        this.successMessage = successMessage;
    }

    public String getExpireDate() {
        return this.expireDate;
    }

    public void setExpireDate(final String expireDate) {
        this.expireDate = expireDate;
    }

    public LocalDateTime getExpireDateAsDate() {
        return LocalDateTime.parse(this.expireDate, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }


}
