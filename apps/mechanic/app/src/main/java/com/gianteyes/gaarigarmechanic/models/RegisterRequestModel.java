package com.gianteyes.gaarigarmechanic.models;

public class RegisterRequestModel {

    private String firstName;
    private String lastname;
    private String phone;
    private String password;
    private String email;

    public RegisterRequestModel(String firstName, String lastname, String phone, String password, String email) {
        this.firstName = firstName;
        this.lastname = lastname;
        this.phone = phone;
        this.password = password;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
