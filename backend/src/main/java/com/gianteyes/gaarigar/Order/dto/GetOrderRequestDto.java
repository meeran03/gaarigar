package com.gianteyes.gaarigar.Order.dto;

import com.gianteyes.gaarigar.Order.OrderStatus;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
public class GetOrderRequestDto {
    @NotNull
    private Long customerId;
    private List<OrderStatus> statuses;
}
