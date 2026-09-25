package com.gianteyes.gaarigar.Order.MechanicOrder;

import com.gianteyes.gaarigar.Order.StandardServiceOrder.StandardServiceOrder;
import com.gianteyes.gaarigar.Order.StandardServiceOrder.dto.request.CreateStandardServiceOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/mechanic-order")
public class MechanicOrderController {
    @Autowired
    private MechanicOrderService mechanicOrderService;

}
