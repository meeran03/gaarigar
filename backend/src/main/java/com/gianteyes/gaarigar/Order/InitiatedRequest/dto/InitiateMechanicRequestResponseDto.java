package com.gianteyes.gaarigar.Order.InitiatedRequest.dto;


import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.mechanic.MechanicType;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class InitiateMechanicRequestResponseDto {
    private Long id;
    private List<NearbyMechanic> nearbyProviders;


    public void setNearbyMechanicsFromMechanics(List<MechanicModel> mechanic) {
        this.nearbyProviders = new ArrayList<>();
        for (MechanicModel mechanicModel : mechanic) {
            this.nearbyProviders.add(NearbyMechanic.builder()
                    .id(mechanicModel.getId())
                    .name(mechanicModel.getFirstName() + " " + mechanicModel.getLastName())
                    .phone(mechanicModel.getPhone())
                    .image(mechanicModel.getImage())
                    .location(Location.builder().latitude(mechanicModel.getLocation().getY()).longitude(mechanicModel.getLocation().getX()).build())
                    .mechanicType(mechanicModel.getType())
                    .build());
        }
    }


    @Data
    @Builder
    static public class NearbyMechanic {
        private Long id;
        private String name;
        private String phone;
        private String image;
        private Location location;
        private MechanicType mechanicType;
    }


}
