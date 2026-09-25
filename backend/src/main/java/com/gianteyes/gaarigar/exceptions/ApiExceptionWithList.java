package com.gianteyes.gaarigar.exceptions;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


public class ApiExceptionWithList extends ApiException {


    private  final List<String> data;
    public ApiExceptionWithList(List<String> data,String message, HttpStatus httpStatus, ZonedDateTime timestamp) {

        super(message, httpStatus, timestamp);
        this.setMessage("Validation failed");
        this.data = data;


    }

    public List<String> getData() {
        return data;
    }
}
