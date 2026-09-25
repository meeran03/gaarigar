package com.gianteyes.gaarigar.auth.dto.response;

import com.gianteyes.gaarigar.user.UserType;
import lombok.Data;

@Data
public class RegisterResponseDto {
    private String phone;
    private String firstName;
    private String lastName;
    private UserType userType;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
