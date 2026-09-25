package com.gianteyes.gaarigar.customer.dto.request;

import com.gianteyes.gaarigar.common.Location;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequestDto {
    private Boolean isActive;
    private Boolean isVerified;
    private Long id;
    @NotNull(message = "Email is required")
    private String email;

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

}
