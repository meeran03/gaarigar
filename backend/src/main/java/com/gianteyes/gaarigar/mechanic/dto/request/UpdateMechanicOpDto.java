package com.gianteyes.gaarigar.mechanic.dto.request;

import com.gianteyes.gaarigar.customer.dto.request.UpdateCustomerOpDto;
import com.gianteyes.gaarigar.mechanic.MechanicType;
import lombok.Data;

@Data
public class UpdateMechanicOpDto extends UpdateCustomerOpDto {
    private MechanicType type;
}
