package com.gianteyes.gaarigar.auth.dto;

import com.gianteyes.gaarigar.common.admin.AdminModel;
import com.gianteyes.gaarigar.auth.dto.request.RegisterMechanicRequestDto;
import com.gianteyes.gaarigar.auth.dto.request.RegisterPetrolPumpRequestDto;
import com.gianteyes.gaarigar.auth.dto.request.RegisterRequestDto;
import com.gianteyes.gaarigar.auth.dto.response.*;
import com.gianteyes.gaarigar.customer.CustomerModel;
import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpModel;
import com.gianteyes.gaarigar.user.UserModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    @Autowired
    private ModelMapper modelMapper;

    public UserModel mapRegisterRequestToToUserModel(RegisterRequestDto request) {
        return modelMapper.map(request, UserModel.class);
    }

    public AdminModel mapRegisterRequestToToAdminModel(RegisterRequestDto request) {
        return modelMapper.map(request, AdminModel.class);
    }

    public RegisterAdminResponseDto mapAdminModelToRegisterResponseDto(AdminModel user) {
        return modelMapper.map(user, RegisterAdminResponseDto.class);
    }

    public CustomerModel mapRegisterRequestToToCustomerModel(RegisterRequestDto request) {
        return modelMapper.map(request, CustomerModel.class);
    }

    public RegisterCustomerResponseDto mapCustomerModelToRegisterResponseDto(CustomerModel user) {
        return modelMapper.map(user, RegisterCustomerResponseDto.class);
    }

    public MechanicModel mapRegisterRequestToToMechanicModel(RegisterMechanicRequestDto request) {
        return modelMapper.map(request, MechanicModel.class);
    }

    public RegisterMechanicResponseDto mapMechanicModelToRegisterResponseDto(MechanicModel user) {
        return modelMapper.map(user, RegisterMechanicResponseDto.class);
    }

    public PetrolPumpModel mapRegisterRequestToToPetrolPumpModel(RegisterPetrolPumpRequestDto request) {
        return modelMapper.map(request, PetrolPumpModel.class);
    }

    public RegisterPetrolPumpResponseDto mapPetrolPumpModelToRegisterResponseDto(PetrolPumpModel user) {
        return modelMapper.map(user, RegisterPetrolPumpResponseDto.class);
    }

    public LoginResponseDto mapUserToLoginResponseDto(UserModel user) {
        return modelMapper.map(user, LoginResponseDto.class);
    }


}
