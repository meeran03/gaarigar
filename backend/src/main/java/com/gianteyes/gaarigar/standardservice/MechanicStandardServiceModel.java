package com.gianteyes.gaarigar.standardservice;

import com.gianteyes.gaarigar.mechanic.MechanicModel;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

/*
    This class is used to store the standard services provided by the mechanic
 */
@Getter
@Setter
@Table(
        name = "mechanic_standard_service",
        uniqueConstraints = @UniqueConstraint(name = "uc_mechanic_standard_service", columnNames = {"mechanic_id", "standard_service_id"})
)
@Entity
public class MechanicStandardServiceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "mechanic_id", nullable = false)
//    @JsonIgnore
    private MechanicModel mechanic;

    @ManyToOne
    @JoinColumn(name = "standard_service_id", nullable = false)
//    @JsonIgnore
    private StandardServiceModel standardService;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

}
