package com.example.mechanicapp.Models;

public class PastHistoryModel {
    public PastHistoryModel(String past_history_details) {
        this.past_history_details = past_history_details;
    }

    public String getPast_history_details() {
        return past_history_details;
    }

    public void setPast_history_details(String past_history_details) {
        this.past_history_details = past_history_details;
    }

    private String past_history_details;

}
