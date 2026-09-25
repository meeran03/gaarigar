package com.gianteyes.gaarigarapp.models;

import com.gianteyes.gaarigarapp.models.common.MLocation;

public class InitiateFuelRequestModel {
    private MLocation location;
    private Float litres;
    private String notes;

    public InitiateFuelRequestModel() {
    }

    public InitiateFuelRequestModel(final MLocation location, final Float litres, final String notes) {
        this.location = location;
        this.litres = litres;
        this.notes = notes;
    }

    public MLocation getLocation() {
        return this.location;
    }

    public void setLocation(MLocation location) {
        this.location = location;
    }

    public Float getLitres() {
        return this.litres;
    }

    public void setLitres(Float litres) {
        this.litres = litres;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
