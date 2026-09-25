package com.gianteyes.gaarigar.exceptions;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ApiRequestException {
    public AlreadyExistsException(String object, String key, String value) {
        super(String.format("%s with %s: %s already exists", object, key, value), HttpStatus.BAD_REQUEST);
    }
}
