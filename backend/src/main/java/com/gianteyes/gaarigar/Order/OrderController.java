package com.gianteyes.gaarigar.Order;


import com.gianteyes.gaarigar.Order.StandardServiceOrder.dto.response.PaymentResponseDto;
import com.gianteyes.gaarigar.Order.dto.OrderResponseDto;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/complete/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.order(#id,true)")
    public void completeStandardServiceRequest(@PathVariable Long id, @RequestBody Double price) {
        orderService.markAsCompleted(id, price);
    }

    @PutMapping("/cancel/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.order(#id,false)")
    public void cancel(@PathVariable Long id) {
        this.orderService.cancel(id);
    }

    @GetMapping("/{userId}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.sameUser(#userId)")
    public List<OrderResponseDto> getOrders(@PathVariable("userId") Long userId,
                                            @RequestParam Map<String, String> queryParams) {
        return orderService.getOrders(userId, queryParams);
    }

    @PostMapping("/pay/{orderId}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.order(#orderId,false)")
    public PaymentResponseDto payOrder(@PathVariable Long orderId) throws StripeException {
        return this.orderService.payOrder(orderId);
    }

    @PostMapping("/startOrder/{orderId}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.order(#orderId,true)")
    public OrderResponseDto startOrder(@PathVariable Long orderId) {
        return this.orderService.startOrder(orderId);
    }
}
