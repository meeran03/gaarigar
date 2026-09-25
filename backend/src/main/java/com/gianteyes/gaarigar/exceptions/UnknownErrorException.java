package com.gianteyes.gaarigar.exceptions;

import org.springframework.http.HttpStatus;

public class UnknownErrorException extends ApiRequestException{
    public UnknownErrorException(String object) {
        super(String.format("%s",object), HttpStatus.BAD_REQUEST);
    }
}
