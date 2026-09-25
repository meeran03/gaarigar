package com.gianteyes.gaarigar.auth.dto.request;


import com.gianteyes.gaarigar.mechanic.MechanicType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterMechanicRequestDto extends RegisterRequestDto {
    private Boolean isAvailable;
    private MechanicType type;
}
