package com.gianteyes.gaarigar.mechanic;

import com.gianteyes.gaarigar.user.UserModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@NoArgsConstructor
@Table(name = "mechanic")
@Entity
@Getter
@Setter
public class MechanicModel extends UserModel {
    @Column(name = "isAvailable", nullable = false)
    private Boolean isAvailable = false;

    @Column(name = "rating")
    private Double rating = 5D;

    @Column(name = "type", nullable = false)
    private MechanicType type;

    public MechanicModel(UserModel user) {
        super(user);
    }

    public MechanicModel(MechanicModel user) {
        super(user);
        this.setType(user.getType());
        this.setRating(user.getRating());
        this.setIsAvailable(user.getIsAvailable());
    }

    public Long getMechanicId() {
        return this.getId();
    }

    public void setMechanicId(Long id) {
        this.setId(id);
    }
}
