package com.gianteyes.gaarigarapp.models;

import java.util.List;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class InitiateRequestResponseModel {
    private Long id;
    private List<ServiceProvider> nearbyProviders;
}
