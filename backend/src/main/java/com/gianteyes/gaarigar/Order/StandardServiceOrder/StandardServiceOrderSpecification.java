package com.gianteyes.gaarigar.Order.StandardServiceOrder;

import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.utils.BaseSpecification;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

public class StandardServiceOrderSpecification extends BaseSpecification<StandardServiceOrder> {
    public static Specification<StandardServiceOrder> hasMechanicIdOrCustomerId(Long userId) {
        return (root, query, builder) -> {
            Join<StandardServiceOrder, MechanicModel> join = root.join("mechanic");
            return builder.or(builder.equal(join.get("id"), userId), builder.equal(root.get("customer").get("id"), userId));
        };
    }

}
