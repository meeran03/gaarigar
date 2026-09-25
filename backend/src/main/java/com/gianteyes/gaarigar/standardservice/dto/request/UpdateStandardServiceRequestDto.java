package com.gianteyes.gaarigar.standardservice.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStandardServiceRequestDto {
    private Long id;
    private String name;
    private String description;
    private String image;
    private Long categoryId;
    private MultipartFile imageFile;
}
