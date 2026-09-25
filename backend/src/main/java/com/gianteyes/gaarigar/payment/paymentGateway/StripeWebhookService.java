package com.gianteyes.gaarigar.payment.paymentGateway;

import com.gianteyes.gaarigar.Order.FuelDeliveryOrder.FuelDeliveryOrderModel;
import com.gianteyes.gaarigar.Order.FuelDeliveryOrder.FuelOrderService;
import com.gianteyes.gaarigar.Order.MechanicOrder.MechanicOrderModel;
import com.gianteyes.gaarigar.Order.MechanicOrder.MechanicOrderService;
import com.gianteyes.gaarigar.Order.OrderModel;
import com.gianteyes.gaarigar.Order.OrderService;
import com.gianteyes.gaarigar.Order.OrderStatus;
import com.gianteyes.gaarigar.Order.OrderType;
import com.gianteyes.gaarigar.Order.StandardServiceOrder.StandardServiceOrder;
import com.gianteyes.gaarigar.Order.StandardServiceOrder.StandardServiceOrderService;
import com.gianteyes.gaarigar.payment.PaymentDetailModel;
import com.gianteyes.gaarigar.payment.PaymentDetailService;
import com.gianteyes.gaarigar.user.UserModel;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.Webhook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StripeWebhookService {

    private final Logger logger = LogManager.getLogger(StripeWebhookController.class);
    @Autowired
    PaymentDetailService paymentDetailService;
    @Autowired
    MechanicOrderService mechanicOrderService;
    @Autowired
    FuelOrderService fuelOrderService;
    @Autowired
    StandardServiceOrderService standardServiceOrderService;
    @Autowired
    OrderService orderService;
    @Value("${stripe.webhook.secret}")
    private String endPointSecret;
    private StripePaymentGateway stripePaymentGateway;

    public String getDestinationStripeId(OrderModel orderModel) throws StripeException {
        Long orderId = orderModel.getId();
        UserModel transferTo;
        if (orderModel.getOrderType() == OrderType.FUEL_DELIVERY) {
            Optional<FuelDeliveryOrderModel> model = fuelOrderService.getFuelOrder(orderId);
            transferTo = model.get().getPetrolPump();
        } else if (orderModel.getOrderType() == OrderType.MECHANIC) {
            Optional<MechanicOrderModel> model = mechanicOrderService.getMechanicOrder(orderId);
            transferTo = model.get().getMechanic();
        } else {
            Optional<StandardServiceOrder> model = standardServiceOrderService.getStandardOrder(orderId);
            transferTo = model.get().getMechanicStandardService().getMechanic();
        }
        String stripeId = null;
        if (!stripePaymentGateway.checkUserAtGateway(transferTo)) {
            stripeId = stripePaymentGateway.createUserAtGateway(transferTo);
        } else {
            stripeId = stripePaymentGateway.getUserIdAtGateway(transferTo);
        }
        return stripeId;
    }


    public String handleEvent(String payload, String sigHeader) throws StripeException {
        if (sigHeader == null) {
            return "";
        }

        Event event;
        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, endPointSecret
            );
        } catch (SignatureVerificationException e) {
            logger.info("⚠️  Webhook error while validating signature.");
            return "";
        }
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                String transactionId = paymentIntent.getId();
                PaymentDetailModel paymentDetailModel = paymentDetailService.updateDetail(transactionId, "succeeded");
                logger.info("Payment for {}, {} succeeded.", paymentIntent.getId(), paymentIntent.getAmount());
                OrderModel order = paymentDetailModel.getOrderModel();
                order.setStatus(OrderStatus.COMPLETED);
                orderService.save(order);
                break;
            case "payment_intent.created":
                paymentIntent = (PaymentIntent) stripeObject;
                transactionId = paymentIntent.getId();
                paymentDetailModel = paymentDetailService.updateDetail(transactionId, "created");
                logger.info("Payment for {}, {} created.", paymentIntent.getId(), paymentIntent.getAmount());
                break;
            case "payment_intent.payment_failed":
                paymentIntent = (PaymentIntent) stripeObject;
                transactionId = paymentIntent.getId();
                paymentDetailModel = paymentDetailService.updateDetail(transactionId, "failed");
                logger.info("Payment for {}, {} failed.", paymentIntent.getId(), paymentIntent.getAmount());
                break;
            case "payment_intent.canceled":
                paymentIntent = (PaymentIntent) stripeObject;
                transactionId = paymentIntent.getId();
                paymentDetailModel = paymentDetailService.updateDetail(transactionId, "cancelled");
                logger.info("Payment for {}, {} cancelled.", paymentIntent.getId(), paymentIntent.getAmount());
                break;
            case "transfer_created":
                Transfer transfer = (Transfer) stripeObject;
                logger.info("Transfer for {}, {} succeeded.", transfer.getDestination(), transfer.getAmount());
            default:
                logger.warn("Unhandled event type: {}", event.getType());
                break;
        }
        return "";
    }
}
