package com.gianteyes.gaarigarapp.models;

import com.gianteyes.gaarigarapp.models.common.MLocation;
import com.gianteyes.gaarigarapp.models.common.OrderType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitiateRequestModel {
    private MLocation location;
    private String mechanicType;
    private String notes;
    private Long customer;
    private String paymentMethod;
    private Float noOfLitres;
    private OrderType requestType;
}
