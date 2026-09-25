package com.gianteyes.gaarigar.rating;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gianteyes.gaarigar.user.UserModel;
import com.google.firebase.database.annotations.NotNull;
import com.stripe.model.Customer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rating")
public class RatingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
   // @JoinColumn(name = "user_model_id", nullable = false)
    @JsonIgnore
    private UserModel ratedBy;

    @ManyToOne
   // @JoinColumn(name = "user_model_id", nullable = false)
    @JsonIgnore
    private UserModel ratedTo;


}
