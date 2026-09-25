package com.gianteyes.gaarigar.Order.InitiatedRequest;

import com.gianteyes.gaarigar.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InitiateRequestRepository extends JpaRepository<InitiateRequestModel, Long> {
    List<InitiateRequestModel> findAllByCustomer_IdAndOrderStatusIn(Long customerId, List<OrderStatus> statusList);
}
