package com.gianteyes.gaarigar.Order.InitiatedRequest;

import com.gianteyes.gaarigar.Order.OrderStatus;
import com.gianteyes.gaarigar.Order.OrderType;
import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.customer.CustomerModel;
import com.gianteyes.gaarigar.mechanic.MechanicType;
import com.gianteyes.gaarigar.payment.PaymentMethod;
import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "InitiatedRequest")
@Entity
@NoArgsConstructor
public class InitiateRequestModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private CustomerModel customer;
    @Column(columnDefinition = "TEXT")
    private String notes;
    @Embedded
    private Location location;
    private OrderType orderType;
    private Float noOfLitres;
    private MechanicType mechanicType;
    @Column(name = "order_status")
    private OrderStatus orderStatus;
    @NotNull(message = "specify payment method")
    private PaymentMethod paymentMethod;
    private LocalDateTime requestedAt;
}
