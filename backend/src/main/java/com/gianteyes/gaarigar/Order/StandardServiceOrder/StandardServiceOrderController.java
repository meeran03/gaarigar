package com.gianteyes.gaarigar.Order.StandardServiceOrder;

import com.gianteyes.gaarigar.Order.StandardServiceOrder.dto.request.CreateStandardServiceOrderRequest;
import com.gianteyes.gaarigar.Order.dto.OrderResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/standard-order")
public class StandardServiceOrderController {

    @Autowired
    private StandardServiceOrderService standardServiceOrderService;

    @PostMapping("/create")
    @org.springframework.security.access.prepost.PreAuthorize("@access.role('CUSTOMER')")
    public OrderResponseDto create(@Valid @RequestBody CreateStandardServiceOrderRequest request) {
        return this.standardServiceOrderService.createStandardServiceOrder(request);
    }

    @PostMapping("/accept/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.order(#id,true)")
    public OrderResponseDto acceptStandardServiceRequest(@PathVariable Long id) {
        return standardServiceOrderService.accept(id);
    }

}
