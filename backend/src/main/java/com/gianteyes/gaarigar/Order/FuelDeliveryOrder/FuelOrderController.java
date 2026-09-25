package com.gianteyes.gaarigar.Order.FuelDeliveryOrder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/fuel-order")
public class FuelOrderController {

    @Autowired
    private FuelOrderService fuelOrderService;
}
