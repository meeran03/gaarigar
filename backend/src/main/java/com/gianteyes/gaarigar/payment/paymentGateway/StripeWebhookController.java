package com.gianteyes.gaarigar.payment.paymentGateway;

import com.gianteyes.gaarigar.payment.PaymentDetailModel;
import com.gianteyes.gaarigar.payment.PaymentDetailService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeWebhookController {

    private Logger logger = LogManager.getLogger(StripeWebhookController.class);
    @Autowired
    PaymentDetailService paymentDetailService;
    @Autowired
    private StripeWebhookService stripeWebhookService;

    @PostMapping("/stripe/events")
    public String handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws StripeException {
        return this.stripeWebhookService.handleEvent(payload, sigHeader);
    }
}
