package com.gianteyes.gaarigar.Order.InitiatedRequest.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class AcceptFuelDeliveryRequestDto {
    @NotNull
    private Long requestId;
    @NotNull
    private Long petrolPumpId;
    @NotNull
    private float literPrice;
    @NotNull
    private float distance;

}
