package com.gianteyes.gaarigar.common;

import lombok.*;

import jakarta.persistence.Embeddable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Location {
    private Double latitude;
    private Double longitude;

    public String convertToString() {
        // returns a string POINT GIS format
        return String.format("POINT (%f %f)", this.longitude, this.latitude);
    }
}
