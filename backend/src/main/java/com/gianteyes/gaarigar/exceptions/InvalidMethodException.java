package com.gianteyes.gaarigar.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidMethodException extends ApiRequestException{

    public InvalidMethodException(Long value) {
        super(String.format("Payment Method for Order with id %s is ONLINE PAYMENT", value), HttpStatus.NOT_ACCEPTABLE);
    }
}
