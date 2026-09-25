package com.gianteyes.gaarigarmechanic.models;

import com.gianteyes.gaarigarmechanic.models.common.OrderType;
import com.gianteyes.gaarigarmechanic.models.common.OrderUser;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OrderModel {
    private Long orderId;
    private Double price;
    private String status;
    private String requestedAt;
    private String acceptedAt;
    private String completedAt;
    private OrderType type;
    private String notes;
    private String paymentMethod;
    private OrderUser customer;
    private OrderUser mechanic;
    private OrderUser petrolPump;
    private StandardService standardService;
    private Float litres;
}
