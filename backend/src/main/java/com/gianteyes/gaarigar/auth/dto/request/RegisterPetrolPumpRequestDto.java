package com.gianteyes.gaarigar.auth.dto.request;

import com.gianteyes.gaarigar.petrolpump.FuelType;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class RegisterPetrolPumpRequestDto extends RegisterRequestDto {

    private Boolean isAvailable;
    @NotNull(message="Address is required")
    private String address;
}
