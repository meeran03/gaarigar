package com.gianteyes.gaarigar.rating.dto;

import com.gianteyes.gaarigar.customer.CustomerModel;
import com.gianteyes.gaarigar.user.UserModel;
import com.stripe.model.Customer;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data

public class RatingRequestDto {

    @NotNull(message="rating is required")
    private Double rating;
    private String comment;

    @NotNull(message="RatedBy is req")
    private Long ratedBy;

    @NotNull(message="RatedTo is required")
    private Long ratedTo;
}
