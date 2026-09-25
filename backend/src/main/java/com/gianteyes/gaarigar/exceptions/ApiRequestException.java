package com.gianteyes.gaarigar.exceptions;

import org.springframework.http.HttpStatus;

public class ApiRequestException extends RuntimeException {

    private HttpStatus status = HttpStatus.BAD_REQUEST;
    public ApiRequestException(String message, HttpStatus status) {
        super(message);
        if (status != null) {
            this.status = status;
        }
    }

    public ApiRequestException(String message) {
        super(message);
    }
    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
