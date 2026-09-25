package com.gianteyes.gaarigarmechanic.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private String customerSecret;
    private String customerId;
    private String publishableKey;
    private String ephemeralKey;
}
