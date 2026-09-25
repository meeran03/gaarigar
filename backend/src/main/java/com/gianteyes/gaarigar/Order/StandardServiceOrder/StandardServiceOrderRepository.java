package com.gianteyes.gaarigar.Order.StandardServiceOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StandardServiceOrderRepository extends JpaRepository<StandardServiceOrder, Long>,
        JpaSpecificationExecutor<StandardServiceOrder> {
}
