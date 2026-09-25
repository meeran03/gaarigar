package com.gianteyes.gaarigar.Order.dto;

import com.gianteyes.gaarigar.common.Location;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderUser {
    Long id;
    String name;
    String image;
    String phone;
    Location location;
    Double rating;
}
