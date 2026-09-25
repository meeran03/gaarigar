package com.gianteyes.gaarigar.common.admin;


import com.gianteyes.gaarigar.user.UserModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Getter
@Setter
@Table(name = "admin")
@Entity
@NoArgsConstructor
public class AdminModel extends UserModel {
    public AdminModel(UserModel user) {
        super(user);
    }


}
