package com.gianteyes.gaarigar.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    long countByStatusAndCompletedAtBetween(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);

    long countByStatusAndCancelledAtBetween(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);

    List<OrderModel> findAllByCustomer_IdAndStatusIn(Long customerId, List<OrderStatus> status);
}
