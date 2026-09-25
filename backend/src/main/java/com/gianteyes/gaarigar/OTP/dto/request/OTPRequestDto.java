package com.gianteyes.gaarigar.OTP.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OTPRequestDto {
    @NotNull(message = "Phone Number is Required")
    private String phoneNumber;
}
