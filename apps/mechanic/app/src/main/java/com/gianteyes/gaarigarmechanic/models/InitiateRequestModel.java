package com.gianteyes.gaarigarmechanic.models;

import com.gianteyes.gaarigarmechanic.models.common.MLocation;
import com.gianteyes.gaarigarmechanic.models.common.OrderType;

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
    private Float litres;
    private OrderType requestType;
}
