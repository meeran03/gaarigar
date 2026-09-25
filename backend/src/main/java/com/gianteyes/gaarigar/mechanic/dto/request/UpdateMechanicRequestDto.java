package com.gianteyes.gaarigar.mechanic.dto.request;

import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.mechanic.MechanicType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateMechanicRequestDto {
    private Boolean isActive;
    private Boolean isVerified;
    private Long id;
    @NotNull(message = "First Name is required")
    private String firstName;
    @NotNull(message = "Last Name is required")
    private String lastName;
    private String image;
    private MultipartFile imageFile;

    @NotNull(message = "Password is required")
    private String password;
    @NotNull(message = "Confirm Password is required")
    private String confirmPassword;
    @NotNull(message = "Location is required")
    private Location location;
    @NotNull(message = "Phone Number is required")
    private String phone;
    private Boolean isAvailable;
    private Double rating;
    private MechanicType type;
}
