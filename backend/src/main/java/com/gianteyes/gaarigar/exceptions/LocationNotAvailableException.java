package com.gianteyes.gaarigar.exceptions;

import org.springframework.http.HttpStatus;

public class LocationNotAvailableException extends ApiRequestException {
    private static final String message = "Location not available";
    public LocationNotAvailableException() {
        super(message, HttpStatus.NOT_FOUND);
    }
}
