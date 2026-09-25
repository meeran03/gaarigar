package com.gianteyes.gaarigar.Order.StandardServiceOrder.dto.request;

import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.payment.PaymentMethod;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class CreateStandardServiceOrderRequest {
    @NotNull
    private Long mechanicStandardServiceId;
    @NotNull
    private Location location;
    private String notes;
    @NotNull
    private PaymentMethod paymentMethod;
}
