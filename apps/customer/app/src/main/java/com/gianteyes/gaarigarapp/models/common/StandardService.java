package com.gianteyes.gaarigarapp.models.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StandardService {
    Long id;
    String name;
    String image;
    String description;
    Double price;
}
