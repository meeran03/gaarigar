package com.gianteyes.gaarigar.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gianteyes.gaarigar.Order.OrderModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_detail")
@Entity
public class PaymentDetailModel {

    @Column(name = "transactionId", nullable = false)
    @Id
    private String transactionId;

    @Column(name = "gatewayId", nullable = false)
    private String customerGatewayId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "status")
    private PaymentStatus status;

    @OneToOne
    @JoinColumn(name = "service_request_id", nullable = false)
    @JsonIgnore
    private OrderModel orderModel;

}
