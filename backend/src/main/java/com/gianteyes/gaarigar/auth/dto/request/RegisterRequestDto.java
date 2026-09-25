package com.gianteyes.gaarigar.auth.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@Getter
@Setter
public class RegisterRequestDto {

    @NotNull(message = "Phone number is required")
    // phone number should be 12 digits and should start with +92
    @Pattern(regexp = "^\\+\\d{12}$", message = "Phone number is invalid")
    private String phone;
    @NotBlank(message = "Password cannot be empty")
    @NotNull(message = "Password is required")
    private String password;
    @NotBlank(message = "First Name cannot be empty")
    @NotNull(message = "First Name is required")
    private String firstName;
    @NotBlank(message = "Last Name cannot be empty")
    @NotNull(message = "Last Name is required")
    private String lastName;
}
