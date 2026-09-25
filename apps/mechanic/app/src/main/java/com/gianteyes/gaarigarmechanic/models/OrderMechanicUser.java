package com.gianteyes.gaarigarmechanic.models;

import com.gianteyes.gaarigarmechanic.models.common.MLocation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMechanicUser {
    Long id;
    String name;
    String image;
    String phone;
    MLocation location;
    Float rating;
}
