package com.gianteyes.gaarigar.Order;

import com.gianteyes.gaarigar.Order.FuelDeliveryOrder.FuelOrderService;
import com.gianteyes.gaarigar.Order.InitiatedRequest.InitiateRequestService;
import com.gianteyes.gaarigar.Order.MechanicOrder.MechanicOrderService;
import com.gianteyes.gaarigar.Order.StandardServiceOrder.StandardServiceOrderService;
import com.gianteyes.gaarigar.Order.StandardServiceOrder.dto.response.PaymentResponseDto;
import com.gianteyes.gaarigar.Order.dto.OrderResponseDto;
import com.gianteyes.gaarigar.Order.dto.OrderUser;
import com.gianteyes.gaarigar.exceptions.InvalidPaymentMethodException;
import com.gianteyes.gaarigar.exceptions.InvalidRequestStatusException;
import com.gianteyes.gaarigar.exceptions.ResourceNotFoundException;
import com.gianteyes.gaarigar.notification.Note;
import com.gianteyes.gaarigar.notification.NotificationUtil;
import com.gianteyes.gaarigar.payment.PaymentDetailModel;
import com.gianteyes.gaarigar.payment.PaymentDetailService;
import com.gianteyes.gaarigar.payment.PaymentMethod;
import com.gianteyes.gaarigar.payment.PaymentStatus;
import com.gianteyes.gaarigar.payment.paymentGateway.StripePaymentGateway;
import com.gianteyes.gaarigar.user.UserModel;
import com.gianteyes.gaarigar.utils.FileUpload;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.LocalDateTime.now;

@Service
public class OrderService {
    @Autowired
    FileUpload fileUpload;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StandardServiceOrderService standardServiceOrderService;
    @Autowired
    private FuelOrderService fuelOrderService;
    @Autowired
    private MechanicOrderService mechanicOrderService;

    @Autowired
    private InitiateRequestService initiateRequestService;
    @Autowired
    private NotificationUtil notificationUtil;
    @Autowired
    private StripePaymentGateway stripePaymentGateway;
    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;
    @Autowired
    private PaymentDetailService paymentDetailService;
    @Autowired
    private ModelMapper modelMapper;

    public Long getCompletedOrdersCount(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.countByStatusAndCompletedAtBetween(OrderStatus.COMPLETED, startDate, endDate);
    }

    public Long getCancelledOrdersCount(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.countByStatusAndCancelledAtBetween(OrderStatus.CANCELLED, startDate, endDate);
    }

    private Note createCancelledNoteForCustomer(OrderModel orderModel) {
        return Note.builder()
                .subject("Order Cancelled")
                .content("Your  order for " + orderModel.getOrderType() + " has been cancelled")
                .data(new HashMap<>() {{
                    put("type", "order");
                    put("id", orderModel.getId().toString());
                    put("email", orderModel.getCustomer().getEmail());
                    put("payment method", orderModel.getPaymentMethod().toString());
                }})
                .templateName("OrderPlacedNotification")
                .mailSubject("Order cancelled")
                .user(orderModel.getCustomer())
                .token(orderModel.getCustomer().getFcmToken()).build();
    }

    @Transactional
    public void cancel(Long orderId) {

        OrderModel order = this.orderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() != OrderStatus.COMPLETED && order.getStatus() != OrderStatus.REJECTED) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setCancelledAt(LocalDateTime.now());
            orderRepository.save(order);
            notificationUtil.sendNotification(createCancelledNoteForCustomer(order));
        } else {
            throw new InvalidRequestStatusException(orderId, "has been completed or cancelled");
        }
    }

    public void markAsCompleted(Long orderId, Double price) {
        OrderModel order = this.orderRepository.findById(orderId).orElseThrow();
        if (order.getPaymentMethod().equals(PaymentMethod.CASH)) {
            if (order.getStatus() == OrderStatus.IN_PROGRESS && order.getStatus() != OrderStatus.REJECTED) {
                if (order.getOrderType() == OrderType.FUEL_DELIVERY || order.getOrderType() == OrderType.STANDARD_SERVICE) {
                    if (!price.equals(order.getPrice())) {
                        throw new ResourceNotFoundException("Order", "price", order.getPrice());
                    }
                } else {
                    order.setPrice(price);
                }
                order.setStatus(OrderStatus.COMPLETED);
               LocalDateTime completedAt = now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES);
                order.setCompletedAt(completedAt);
                this.orderRepository.save(order);
            } else throw new RuntimeException("Invalid Current Status");
        } else {
            if (order.getPrice() == null) {
                order.setPrice(price);
                this.orderRepository.save(order);
            }
        }
    }


    public List<OrderResponseDto> getOrders(Long userId, Map<String, String> queryParams) {
        List<OrderResponseDto> orders = new ArrayList<>();
        orders.addAll(standardServiceOrderService.getOrdersByQuery(userId, queryParams));
        orders.addAll(fuelOrderService.getOrdersByQuery(userId, queryParams));
        orders.addAll(mechanicOrderService.getOrdersByQuery(userId, queryParams));
        return orders;

    }

    public PaymentResponseDto payOrder(Long orderId) throws StripeException {
        OrderModel order = this.orderRepository.findById(orderId).orElseThrow();
        if (!(order.getStatus() != OrderStatus.ACCEPTED || order.getStatus() != OrderStatus.IN_PROGRESS)) {
            throw new InvalidRequestStatusException("Cannot pay for", orderId);
        }
        Optional<PaymentDetailModel> paymentDetailModel = paymentDetailService.findByOrderId(orderId);
        if (paymentDetailModel.isPresent() && (paymentDetailModel.get().getStatus() != PaymentStatus.FAILED || paymentDetailModel.get().getStatus() != PaymentStatus.CANCELLED)) {
            throw new InvalidRequestStatusException("Payment pending", orderId);
        }
        if (order.getPaymentMethod().toString() != "ONLINE_PAYMENT") {
            throw new InvalidPaymentMethodException("Online Payment Not Supported ", orderId);
        }
        UserModel user = order.getCustomer();
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        String stripeId;
        if (user.getStripeId() == null || !stripePaymentGateway.checkUserAtGateway(user)) {
            stripeId = stripePaymentGateway.createUserAtGateway(user);
        } else {
            stripeId = stripePaymentGateway.getUserIdAtGateway(user);
        }
        PaymentIntent intent = stripePaymentGateway.createPaymentIntent(order, stripeId);

        paymentResponseDto.setCustomerId(stripeId);
        paymentResponseDto.setPublishableKey(stripePublishableKey);
        paymentResponseDto.setCustomerSecret(intent.getClientSecret());

        PaymentDetailModel payment;
        if (paymentDetailModel.isEmpty())
            payment = new PaymentDetailModel();
        else {
            payment = paymentDetailModel.get();
        }
        payment.setStatus(PaymentStatus.valueOf("PENDING"));
        payment.setTransactionId(intent.getId());
        payment.setCustomerGatewayId(stripeId);
        payment.setCreatedAt(LocalDateTime.now(ZoneId.systemDefault()));
        payment.setOrderModel(order);
        payment.setAmount(order.getPrice());
        paymentDetailService.createDetail(payment);
        return paymentResponseDto;
    }

    public OrderResponseDto mapOrderToResponse(OrderModel order) {
        OrderResponseDto res = modelMapper.map(order, OrderResponseDto.class);
        res.setCustomer(
                modelMapper.map(order.getCustomer(), OrderUser.class)
        );
        res.getCustomer().setName(
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName()
        );
        res.getCustomer().setImage(
                fileUpload.generateUrl(order.getCustomer().getImage())
        );
        res.getCustomer().setLocation(
                order.getCustomerLocation()
        );
        return res;
    }

    public OrderResponseDto startOrder(Long orderId) {
        Optional<OrderModel> order = orderRepository.findById(orderId);
        if (order.isEmpty())
            throw new ResourceNotFoundException("Order", "id", orderId);
        else {
            if (( order.get().getStatus() == OrderStatus.ACCEPTED) || (order.get().getOrderType() == OrderType.STANDARD_SERVICE && order.get().getStatus() == OrderStatus.REQUESTED)) {
                order.get().setStatus(OrderStatus.IN_PROGRESS);
                OrderModel updated = orderRepository.save(order.get());
                return this.mapOrderToResponse(updated);
            } else {
                throw new InvalidRequestStatusException("Invalid Status for", orderId);
            }
        }
    }

    public void save(OrderModel order) {
        orderRepository.save(order);
    }
}
