package com.gianteyes.gaarigar.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidPaymentMethodException extends ApiRequestException{

    public InvalidPaymentMethodException(Long value) {
        super(String.format("Payment Method for Order with id %s is ONLINE PAYMENT", value), HttpStatus.NOT_ACCEPTABLE);
    }

    public InvalidPaymentMethodException(String key, Long value) {
        super(String.format("%s for Order with id: %s ",key, value), HttpStatus.NOT_ACCEPTABLE);
    }
}
