package com.gianteyes.gaarigar.Order.StandardServiceOrder;

import com.gianteyes.gaarigar.Order.OrderModel;
import com.gianteyes.gaarigar.standardservice.MechanicStandardServiceModel;
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
@Table(name = "StandardServiceOrder")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StandardServiceOrder extends OrderModel {
    @ManyToOne
    private MechanicStandardServiceModel mechanicStandardService;
}
