package com.gianteyes.gaarigar.exceptions;

import com.gianteyes.gaarigar.user.UserType;
import org.springframework.http.HttpStatus;

public class InvalidUserTypeException extends ApiRequestException {

    public InvalidUserTypeException(UserType object) {
        super(String.format("Error! UserType: %s cannot be rated", object), HttpStatus.BAD_REQUEST);
    }

}
