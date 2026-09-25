package com.gianteyes.gaarigar.Order.MechanicOrder;

import com.gianteyes.gaarigar.utils.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

public class MechanicOrderSpecification extends BaseSpecification<MechanicOrderModel> {
    public static Specification<MechanicOrderModel> hasMechanicIdOrCustomerId(Long userId) {
        return (root, query, builder) -> {
            return builder.or(builder.equal(root.get("mechanic").get("id"), userId), builder.equal(root.get("customer").get("id"), userId));
        };
    }
}
