package com.gianteyes.gaarigar.Order.MechanicOrder;

import com.gianteyes.gaarigar.Order.OrderModel;
import com.gianteyes.gaarigar.mechanic.MechanicModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@Table(name = "MechanicOrder")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MechanicOrderModel extends OrderModel {
    @ManyToOne
    private MechanicModel mechanic;
    private String notes;

    public MechanicOrderModel(OrderModel order) {
        super(order);
    }
}
