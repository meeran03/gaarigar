package com.gianteyes.gaarigar.payment.paymentGateway;

import com.gianteyes.gaarigar.Order.OrderModel;
import com.gianteyes.gaarigar.exceptions.UnknownErrorException;
import com.gianteyes.gaarigar.user.UserModel;
import com.gianteyes.gaarigar.user.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class StripePaymentGateway implements IPaymentGateway {
    @org.springframework.beans.factory.annotation.Value("${gaarigar.integrations.enabled:false}")
    private boolean integrationsEnabled;

    @Autowired
    UserRepository userRepository;

    public PaymentIntent createPaymentIntent(OrderModel order, String customerId) throws StripeException {
        if (!integrationsEnabled) throw new IllegalStateException("Payments are not configured");
        if (order.getPrice() == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                .setCurrency("usd")
                .setAmount((long) (order.getPrice() * 100))
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build())
                .setCustomer(customerId)
                .build();
        PaymentIntent intent = PaymentIntent.create(createParams);
        return intent;
    }


    @Override
    public String createUserAtGateway(UserModel user) {
        if (!integrationsEnabled) throw new IllegalStateException("Payments are not configured");
        Map<String, Object> paramsCustomer = new HashMap<>();
        paramsCustomer.put(
                "name",
                user.getFirstName()
        );
        paramsCustomer.put(
                "phone",
                user.getPhone()
        );
        Customer customerInStripe = null;
        try {
            customerInStripe = Customer.create(paramsCustomer);
        } catch (StripeException e) {
            throw new UnknownErrorException("An unknown error has occurred");
        }
        String customerStripeID = customerInStripe.getId();
        user.setStripeId(customerStripeID);
        userRepository.save(user);
        return customerStripeID;
    }

    @Override
    public Boolean checkUserAtGateway(UserModel user) {
        return user.getStripeId() != null && !user.getStripeId().isEmpty();
    }

    @Override
    public String getUserIdAtGateway(UserModel user) {
        return user.getStripeId();
    }
}
