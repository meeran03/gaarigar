package com.gianteyes.gaarigar.petrolpump;

import com.gianteyes.gaarigar.user.UserModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Set;

@NoArgsConstructor
@Table(name = "petrol_pump")
@Entity
@Getter
@Setter
public class PetrolPumpModel extends UserModel {
    @Column(name = "isAvailable", nullable = false)
    private Boolean isAvailable;
    @Column(name = "rating")
    private Double rating = 5D;
    @Column(name = "address", nullable = false)
    private String address;
    public PetrolPumpModel(UserModel user) {
        super(user);
    }


}
