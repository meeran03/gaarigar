package com.gianteyes.gaarigar.standardservice.dto.request;


import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CreateMechanicStandardServiceDto {
    @NotNull(message = "Mechanic Id is Required")
    private Long mechanicId;
    @NotNull(message = "Standard Service Id is Required")
    private Long standardServiceId;
    @NotNull(message = "Price is Required")
    private Long price;
}
