package com.gianteyes.gaarigar.Order.MechanicOrder;


import com.gianteyes.gaarigar.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MechanicOrderRepository extends JpaRepository<MechanicOrderModel, Long>,
        JpaSpecificationExecutor<MechanicOrderModel> {
    List<MechanicOrderModel> findAllByCustomer_IdAndStatusIn(Long customerId, List<OrderStatus> status);

}
