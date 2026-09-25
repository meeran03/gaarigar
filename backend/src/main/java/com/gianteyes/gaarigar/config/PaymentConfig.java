package com.gianteyes.gaarigar.config;

import com.gianteyes.gaarigar.payment.paymentGateway.StripePaymentGateway;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class PaymentConfig {
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.publishable.key}")
    private String publishableKey;
    @Bean
    StripePaymentGateway StripePaymentGateway()
    {
        StripePaymentGateway gateway = new StripePaymentGateway();
        Stripe.apiKey = stripeApiKey;

        return gateway;
    }

}
