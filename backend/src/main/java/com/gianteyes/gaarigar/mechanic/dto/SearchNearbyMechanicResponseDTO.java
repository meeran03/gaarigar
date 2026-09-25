package com.gianteyes.gaarigar.mechanic.dto;

import com.gianteyes.gaarigar.mechanic.MechanicModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class SearchNearbyMechanicResponseDTO {
    private Collection<MechanicModel> nearbyMechanics;
}
