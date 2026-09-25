package com.gianteyes.gaarigar.Order.FuelDeliveryOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FuelOrderRepository extends JpaRepository<FuelDeliveryOrderModel, Long>,
        JpaSpecificationExecutor<FuelDeliveryOrderModel> {
}
