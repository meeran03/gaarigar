package com.gianteyes.gaarigar.Order.FuelDeliveryOrder;


import com.gianteyes.gaarigar.utils.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

public class FuelDeliveryOrderSpecification extends BaseSpecification<FuelDeliveryOrderModel> {
    public static Specification<FuelDeliveryOrderModel> hasPetrolPumpIdOrCustomerId(Long userId) {
        return (root, query, builder) -> {
            return builder.or(builder.equal(root.get("petrolPump").get("id"), userId), builder.equal(root.get("customer").get("id"), userId));
        };
    }
}
