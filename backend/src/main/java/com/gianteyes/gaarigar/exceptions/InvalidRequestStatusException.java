package com.gianteyes.gaarigar.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidRequestStatusException extends ApiRequestException{

    public InvalidRequestStatusException(  Long value, String key) {
        super(String.format("Error! Request with id %s: %s ", value, key), HttpStatus.BAD_REQUEST);
    }

    public InvalidRequestStatusException(  String key, Long value) {
        super(String.format("%s order with id %s", value, key), HttpStatus.BAD_REQUEST);
    }
    public InvalidRequestStatusException(  String object, Long value, String key) {
        super(String.format("%s with id %s: %s ", object, value, key), HttpStatus.BAD_REQUEST);
    }

    public InvalidRequestStatusException(String object, String key) {
        super(String.format("%s with %s cannot be accepted ", object, key), HttpStatus.NOT_FOUND);
    }
}
