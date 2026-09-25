package com.gianteyes.gaarigar.standardservice.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStandardServiceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String image;
}
