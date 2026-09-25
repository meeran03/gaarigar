package com.gianteyes.gaarigar.Order.FuelDeliveryOrder;

import com.gianteyes.gaarigar.Order.OrderModel;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpModel;
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
@Table(name = "FuelDeliveryOrder")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "FuelDeliveryOrder")
public class FuelDeliveryOrderModel extends OrderModel {
    @ManyToOne
    private PetrolPumpModel petrolPump;
    private float distance;
    private float litrePrice;
    private float noOfLitres;
}
