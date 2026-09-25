package com.gianteyes.gaarigar.customer.dto;

import com.gianteyes.gaarigar.customer.CustomerModel;
import com.gianteyes.gaarigar.customer.dto.request.UpdateCustomerRequestDto;
import com.gianteyes.gaarigar.utils.GeoUtils;
import org.locationtech.jts.io.ParseException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GeoUtils geoUtils;

    public CustomerModel convertToEntity(UpdateCustomerRequestDto updateCustomerRequestDto) throws ParseException {
        CustomerModel customer = modelMapper.map(updateCustomerRequestDto, CustomerModel.class);
        customer.setLocation(geoUtils.convertLocationToPoint(updateCustomerRequestDto.getLocation()));
        return customer;
    }

    public UpdateCustomerRequestDto convertToDto(CustomerModel customerModel) {
        UpdateCustomerRequestDto customer = modelMapper.map(customerModel, UpdateCustomerRequestDto.class);
        if (customerModel.getLocation() != null) {
            customer.setLocation(geoUtils.convertPointToLocation(customerModel.getLocation()));
        }
        return customer;
    }
}
