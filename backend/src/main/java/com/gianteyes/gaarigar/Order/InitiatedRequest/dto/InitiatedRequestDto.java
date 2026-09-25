package com.gianteyes.gaarigar.Order.InitiatedRequest.dto;

import com.gianteyes.gaarigar.Order.OrderType;
import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.mechanic.MechanicType;
import com.gianteyes.gaarigar.payment.PaymentMethod;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class InitiatedRequestDto {
    @NotNull
    private Long customer;
    private String notes;
    private Location location;
    @NotNull
    private OrderType requestType;
    private Float noOfLitres;
    private MechanicType mechanicType;
    @NotNull
    private PaymentMethod paymentMethod;
}
