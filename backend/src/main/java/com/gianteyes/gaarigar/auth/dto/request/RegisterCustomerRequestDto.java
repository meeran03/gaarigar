package com.gianteyes.gaarigar.auth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterCustomerRequestDto extends RegisterRequestDto {
    private String email;

}
