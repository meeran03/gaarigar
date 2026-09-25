package com.gianteyes.gaarigar.Order.InitiatedRequest.dto;

import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpModel;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
public class InitiateFuelRequestResponseDto {
    private Long id;
    private List<NearbyPetrolPump> nearbyProviders;


    public void setNearbyPetrolPumpsFromPetrolPumps(List<PetrolPumpModel> petrolPump) {
        this.nearbyProviders = new ArrayList<>();
        for (PetrolPumpModel petrolPumpModel : petrolPump) {
            this.nearbyProviders.add(NearbyPetrolPump.builder()
                    .id(petrolPumpModel.getId())
                    .name(petrolPumpModel.getFirstName() + " " + petrolPumpModel.getLastName())
                    .phone(petrolPumpModel.getPhone())
                    .image(petrolPumpModel.getImage())
                    .location(Location.builder().latitude(petrolPumpModel.getLocation().getY()).longitude(petrolPumpModel.getLocation().getX()).build())
                    .build());
        }
    }

    @Data
    @Builder
    static public class NearbyPetrolPump {
        private Long id;
        private String name;
        private String phone;
        private String image;
        private Location location;
    }


}
