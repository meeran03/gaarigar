package com.gianteyes.gaarigar.Order;

import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.customer.CustomerModel;
import com.gianteyes.gaarigar.payment.PaymentDetailModel;
import com.gianteyes.gaarigar.payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Formula;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ServiceRequest")
@Entity
public class OrderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name="status", nullable = false)
    private OrderStatus status;
    private LocalDateTime requestedAt;
    private Double price;

    private OrderType orderType;
    @Transient
    private LocalDateTime timeToComplete;
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
   private PaymentMethod paymentMethod;
    private Location customerLocation;
    private String notes;
    @JoinColumn(name="customer_id")
    @ManyToOne
    private CustomerModel customer;
    public OrderModel(OrderModel order)
    {
        this.id = order.getId();
        this.status = order.getStatus();
        this.requestedAt = order.getRequestedAt();
        this.price = order.getPrice();
        this.timeToComplete = order.getTimeToComplete();
        this.completedAt = order.getCompletedAt();
        this.acceptedAt = order.getAcceptedAt();
        this.cancelledAt = order.getCancelledAt();
        this.paymentMethod = order.getPaymentMethod();
        this.customerLocation = order.getCustomerLocation();
        this.notes = order.getNotes();
        this.customer = order.getCustomer();
    }
}
