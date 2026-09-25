package com.gianteyes.gaarigarapp.models;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateRequestModel {
    private Float rating;
    private Long ratedBy;
    private Long ratedTo;
    private String comment;
}
