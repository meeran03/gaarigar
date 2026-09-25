package com.gianteyes.gaarigar.auth.dto.response;


import com.gianteyes.gaarigar.petrolpump.FuelType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterPetrolPumpResponseDto extends RegisterResponseDto {

    private Double rating;
    private String address;
   // private FuelType fuelType;
}
