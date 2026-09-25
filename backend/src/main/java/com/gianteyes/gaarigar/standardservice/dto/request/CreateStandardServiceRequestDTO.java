package com.gianteyes.gaarigar.standardservice.dto.request;


import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class CreateStandardServiceRequestDTO {
    @NotNull(message = "Standard Service Name is Required")
    private String name;
    @NotNull(message = "Standard Service Description is Required")
    private String description;
    @NotNull(message = "Standard Service Category is Required")
    private Long categoryId;
    private MultipartFile imageFile;
}
