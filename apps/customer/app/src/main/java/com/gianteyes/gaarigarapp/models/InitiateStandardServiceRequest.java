package com.gianteyes.gaarigarapp.models;

import com.gianteyes.gaarigarapp.models.common.MLocation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InitiateStandardServiceRequest {
    private Long mechanicStandardServiceId;
    private MLocation location;
    private String notes;
    private String paymentMethod;
}
