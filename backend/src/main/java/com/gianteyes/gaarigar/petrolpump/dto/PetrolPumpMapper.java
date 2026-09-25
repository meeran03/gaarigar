package com.gianteyes.gaarigar.petrolpump.dto;


import com.gianteyes.gaarigar.petrolpump.PetrolPumpModel;
import com.gianteyes.gaarigar.petrolpump.dto.request.UpdatePetrolPumpRequestDto;
import com.gianteyes.gaarigar.utils.GeoUtils;
import org.locationtech.jts.io.ParseException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PetrolPumpMapper {
    @Autowired
    GeoUtils geoUtils;
    @Autowired
    private ModelMapper modelMapper;

    public PetrolPumpModel convertToEntity(UpdatePetrolPumpRequestDto updatePetrolPumpRequestDto) throws ParseException {
        PetrolPumpModel petrolPump = modelMapper.map(updatePetrolPumpRequestDto, PetrolPumpModel.class);
        petrolPump.setLocation(geoUtils.convertLocationToPoint(updatePetrolPumpRequestDto.getLocation()));
        return petrolPump;
    }

    public UpdatePetrolPumpRequestDto convertToDto(PetrolPumpModel petrolPumpModel) {
        UpdatePetrolPumpRequestDto petrolPump = modelMapper.map(petrolPumpModel, UpdatePetrolPumpRequestDto.class);
        if (petrolPumpModel.getLocation() != null) {
            petrolPump.setLocation(geoUtils.convertPointToLocation(petrolPumpModel.getLocation()));
        }
        petrolPump.setIsActive(petrolPumpModel.getIsActive());
        return petrolPump;
    }
}
