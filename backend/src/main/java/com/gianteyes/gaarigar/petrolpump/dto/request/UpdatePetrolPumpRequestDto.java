package com.gianteyes.gaarigar.petrolpump.dto.request;

import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.petrolpump.FuelType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePetrolPumpRequestDto {
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
    private String address;
    //private Set<FuelType> fuelType;
}
