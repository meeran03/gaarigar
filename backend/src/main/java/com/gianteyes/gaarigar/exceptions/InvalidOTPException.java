package com.gianteyes.gaarigar.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidOTPException extends ApiRequestException {

    public InvalidOTPException(  String key, String value) {
        super(String.format("%s: %s ", key, value), HttpStatus.BAD_REQUEST);
    }

    public InvalidOTPException(  String key) {
        super(String.format("%s",  key), HttpStatus.BAD_REQUEST);
    }

}
