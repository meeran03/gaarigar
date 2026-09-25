package com.gianteyes.gaarigar.OTP.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor

public class ChangePasswordRequestDto {
    @NotNull
   private String newToken;
    @NotNull(message = "Your Phone Number is Required")
   private String phoneNumber;
    @NotNull(message = "Password is Required")
   private String newPassword;

}
