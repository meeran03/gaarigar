package com.gianteyes.gaarigarmechanic.models.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderUser {
    Long id;
    String name;
    String image;
    String phone;
    MLocation location;
    Double rating;
}
