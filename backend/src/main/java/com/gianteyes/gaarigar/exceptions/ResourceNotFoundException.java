package com.gianteyes.gaarigar.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiRequestException {


    public ResourceNotFoundException(String object, String key, String value) {
        super(String.format("%s with %s: %s does not exist", object, key, value), HttpStatus.NOT_FOUND);
    }


    public ResourceNotFoundException(String object, Long val, String key, Long value) {
        super(String.format("%s with %s:  %s by mechanic with id: %s does not exist", object, val, key, value), HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String object, String key, Long value) {
        super(String.format("%s with %s: %s does not exist", object, key, value), HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String object) {
        super(String.format("%s", object), HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String object, String key, Double value) {
        super(String.format("%s with %s: %s does not exist", object, key, value), HttpStatus.NOT_FOUND);
    }

}
