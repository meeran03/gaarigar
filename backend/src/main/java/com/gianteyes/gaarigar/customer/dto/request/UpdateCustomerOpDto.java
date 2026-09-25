package com.gianteyes.gaarigar.customer.dto.request;

import lombok.Data;

@Data
public class UpdateCustomerOpDto {
    private String firstName;
    private String lastName;
    private String password;
}
