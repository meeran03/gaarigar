package com.gianteyes.gaarigar.petrolpump.dto.request;

import com.gianteyes.gaarigar.customer.dto.request.UpdateCustomerOpDto;
import lombok.Data;

@Data
public class UpdatePetrolPumpOpDto extends UpdateCustomerOpDto {
    private String address;
}
