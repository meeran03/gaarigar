package com.gianteyes.gaarigarapp.models;

public class RegisterRequestModel {

    private String firstName;
    private String lastName;
    private String phone;
    private String password;
    private String email;

    public RegisterRequestModel(final String firstName, final String lastname, final String phone, final String password, final String email) {
        this.firstName = firstName;
        lastName = lastname;
        this.phone = phone;
        this.password = password;
        this.email = email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

}
