package com.gianteyes.gaarigarmechanic.models;

public class PastHistoryModel {
    private String past_history_details;

    public PastHistoryModel(final String past_history_details) {
        this.past_history_details = past_history_details;
    }

    public String getPast_history_details() {
        return this.past_history_details;
    }

    public void setPast_history_details(final String past_history_details) {
        this.past_history_details = past_history_details;
    }

}
