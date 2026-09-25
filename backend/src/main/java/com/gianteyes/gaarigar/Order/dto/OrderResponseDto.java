package com.gianteyes.gaarigar.Order.dto;

import com.gianteyes.gaarigar.Order.OrderStatus;
import com.gianteyes.gaarigar.Order.OrderType;
import com.gianteyes.gaarigar.payment.PaymentMethod;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OrderResponseDto {
    private Long orderId;
    private Double price;
    private OrderStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private OrderType type;
    private String notes;
    private PaymentMethod paymentMethod;
    private OrderUser customer;
    private OrderUser mechanic;
    private OrderUser petrolPump;
    private StandardService standardService;
    private Float litres;
}
