package com.gianteyes.gaarigarmechanic.models;

import com.gianteyes.gaarigarmechanic.models.common.MLocation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceProvider {
    private Long id;
    private String name;
    private String phone;
    private String image;
    private MLocation location;
    private String mechanicType;
    private Float rating;
}
