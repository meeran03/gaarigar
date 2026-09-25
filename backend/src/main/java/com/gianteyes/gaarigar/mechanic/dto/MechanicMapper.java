package com.gianteyes.gaarigar.mechanic.dto;

import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.mechanic.dto.request.UpdateMechanicRequestDto;
import com.gianteyes.gaarigar.utils.GeoUtils;
import org.locationtech.jts.io.ParseException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MechanicMapper {
    @Autowired
    GeoUtils geoUtils;
    @Autowired
    private ModelMapper modelMapper;

    public MechanicModel convertToEntity(UpdateMechanicRequestDto updateCustomerRequestDto) throws ParseException {
        MechanicModel mechanic = modelMapper.map(updateCustomerRequestDto, MechanicModel.class);
        mechanic.setLocation(geoUtils.convertLocationToPoint(updateCustomerRequestDto.getLocation()));
        return mechanic;
    }

    public UpdateMechanicRequestDto convertToDto(MechanicModel mechanicModel) {
        UpdateMechanicRequestDto mechanic = modelMapper.map(mechanicModel, UpdateMechanicRequestDto.class);
        if (mechanicModel.getLocation() != null) {
            mechanic.setLocation(geoUtils.convertPointToLocation(mechanicModel.getLocation()));
        }
        mechanic.setIsActive(mechanicModel.getIsActive());
        return mechanic;
    }
}
