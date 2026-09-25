package com.gianteyes.gaarigarapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardServicesResponse {
    Long id;
    Long price;
    OrderMechanicUser mechanic;
    StandardService standardService;
}
