package com.gianteyes.gaarigar.standardservice.dto;

import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.mechanic.MechanicService;
import com.gianteyes.gaarigar.standardservice.MechanicStandardServiceModel;
import com.gianteyes.gaarigar.standardservice.StandardServiceModel;
import com.gianteyes.gaarigar.standardservice.dto.request.CreateMechanicStandardServiceDto;
import com.gianteyes.gaarigar.standardservice.dto.request.CreateStandardServiceRequestDTO;
import com.gianteyes.gaarigar.standardservice.dto.request.UpdateStandardServiceRequestDto;
import com.gianteyes.gaarigar.standardservice.dto.response.CreateStandardServiceResponseDTO;
import com.gianteyes.gaarigar.standardservice.dto.response.SearchMechanicStandardServiceResponseDto;
import com.gianteyes.gaarigar.utils.FileUpload;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StandardServiceMapper {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MechanicService mechanicService;

    @Autowired
    private FileUpload fileUpload;

    public StandardServiceModel mapCreateRequestToStandardServiceModel(CreateStandardServiceRequestDTO request) {
        // we add a property map to map the property category(Long) to categoryModel(CategoryModel) from database
        return modelMapper.map(request, StandardServiceModel.class);
    }

    public MechanicStandardServiceModel mapCreateRequestToMechanicStandardServiceModel(CreateMechanicStandardServiceDto request) {
        MechanicStandardServiceModel result = modelMapper.map(request, MechanicStandardServiceModel.class);
        MechanicModel mechanic = mechanicService.getByUserId(request.getMechanicId());
        result.setMechanic(mechanic);
        return result;
    }

    public CreateStandardServiceResponseDTO mapStandardServiceModelToCreateResponse(StandardServiceModel standardService) {
        return modelMapper.map(standardService, CreateStandardServiceResponseDTO.class);
    }

    public StandardServiceModel mapUpdateRequestToStandardServiceModel(UpdateStandardServiceRequestDto request) {
        return modelMapper.map(request, StandardServiceModel.class);
    }

    public UpdateStandardServiceRequestDto mapStandardServiceModelToUpdateRequest(StandardServiceModel standardService) {
        return modelMapper.map(standardService, UpdateStandardServiceRequestDto.class);
    }

    public SearchMechanicStandardServiceResponseDto mapMechanicStandardServiceModelToSearchResponse(MechanicStandardServiceModel mechanicStandardServiceModel) {
        SearchMechanicStandardServiceResponseDto res = modelMapper.map(mechanicStandardServiceModel, SearchMechanicStandardServiceResponseDto.class);
        res.setPrice(mechanicStandardServiceModel.getPrice());
        res.getStandardService().setImage(fileUpload.generateUrl(mechanicStandardServiceModel.getStandardService().getImage()));
        return res;
    }

    public List<SearchMechanicStandardServiceResponseDto> mapMechanicStandardServiceModelToSearchResponse(List<MechanicStandardServiceModel> list) {

        return list.stream().map(this::mapMechanicStandardServiceModelToSearchResponse).toList();
    }
}
