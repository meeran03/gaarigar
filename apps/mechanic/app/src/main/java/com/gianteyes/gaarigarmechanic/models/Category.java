package com.gianteyes.gaarigarmechanic.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Category {
    private Long id;
    private String name;

    @Override
    public String toString() {
        return name; // What to display in the Spinner list.
    }
}
