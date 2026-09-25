package com.gianteyes.gaarigarapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardService {
    Long id;
    String name;
    String image;
    String description;
    Double price;
}
