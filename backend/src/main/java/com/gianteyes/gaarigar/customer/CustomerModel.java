package com.gianteyes.gaarigar.customer;

import com.gianteyes.gaarigar.user.UserModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "customer")
public class CustomerModel extends UserModel {
    @Column(name = "email")
    @Email
    String email;
    public CustomerModel(UserModel user) {
        super(user);
    }

}
