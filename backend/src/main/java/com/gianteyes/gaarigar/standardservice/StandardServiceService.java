package com.gianteyes.gaarigar.standardservice;

import com.gianteyes.gaarigar.category.CategoryModel;
import com.gianteyes.gaarigar.category.CategoryService;
import com.gianteyes.gaarigar.config.GeneralConfig;
import com.gianteyes.gaarigar.exceptions.ResourceNotFoundException;
import com.gianteyes.gaarigar.standardservice.dao.StandardServiceRepository;
import com.gianteyes.gaarigar.standardservice.dto.StandardServiceMapper;
import com.gianteyes.gaarigar.standardservice.dto.request.CreateStandardServiceRequestDTO;
import com.gianteyes.gaarigar.standardservice.dto.request.UpdateStandardServiceRequestDto;
import com.gianteyes.gaarigar.standardservice.dto.response.CreateStandardServiceResponseDTO;
import com.gianteyes.gaarigar.utils.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class StandardServiceService {
    @Autowired
    FileUpload fileUpload;
    @Autowired
    GeneralConfig config;
    @Autowired
    private StandardServiceRepository standardServiceRepository;
    @Autowired
    private StandardServiceMapper standardServiceMapper;
    @Autowired
    private CategoryService categoryService;

    public CreateStandardServiceResponseDTO create(CreateStandardServiceRequestDTO request) throws IOException {
        StandardServiceModel standardService = standardServiceMapper.mapCreateRequestToStandardServiceModel(request);
        standardService.setId(null);
        if (request.getImageFile() != null && !Objects.equals(request.getImageFile().getOriginalFilename(), "")) {
            standardService.setImage(fileUpload.uploadImage(request.getImageFile(), config.STANDARD_SERVICE_IMAGES_PATH));
        }
        standardService = standardServiceRepository.save(standardService);
        return standardServiceMapper.mapStandardServiceModelToCreateResponse(standardService);
    }

    public Collection<StandardServiceModel> getAll() {
        Collection<StandardServiceModel> list = standardServiceRepository.findAll();
        list.forEach(standardServiceModel -> {
            standardServiceModel.setImage(fileUpload.generateUrl(standardServiceModel.getImage()));
        });
        return list;
    }

    public List<Object> getMostRequestedStandardServices(LocalDateTime startDate, LocalDateTime endDate) {
        return standardServiceRepository.getMostRequestedStandardServices(startDate, endDate);
    }

    public StandardServiceModel get(Long id) {
        return standardServiceRepository.findById(id).orElse(null);
    }

    String uploadCategoryImage(MultipartFile image, String path) throws IOException {
        return fileUpload.uploadImage(image, path);
    }

    public StandardServiceModel update(UpdateStandardServiceRequestDto obj) throws IOException {
        if (obj.getId() == null) {
            return null;
        }
        if (!Objects.equals(obj.getImageFile().getOriginalFilename(), "")) {
            obj.setImage(fileUpload.uploadImage(obj.getImageFile(), config.STANDARD_SERVICE_IMAGES_PATH));
        }
        StandardServiceModel standardService = standardServiceMapper.mapUpdateRequestToStandardServiceModel(obj);
        return standardServiceRepository.save(standardService);
    }

    public UpdateStandardServiceRequestDto getDecoratedForUpdate(Long id) {
        StandardServiceModel standardService = this.get(id);
        UpdateStandardServiceRequestDto updateStandardServiceRequestDto = standardServiceMapper.mapStandardServiceModelToUpdateRequest(standardService);
        if (updateStandardServiceRequestDto.getImage() != null) {
            updateStandardServiceRequestDto.setImage(fileUpload.generateUrl(updateStandardServiceRequestDto.getImage()));
        }
        return updateStandardServiceRequestDto;
    }

    public void delete(Long id) {
        standardServiceRepository.deleteById(id);
    }

    public Collection<StandardServiceModel> getStandardServiceByCategoryId(Long categoryId) {
        CategoryModel categoryModel = categoryService.getCategoryById(categoryId);
        if(categoryModel == null)
            throw new ResourceNotFoundException("Category", "id",categoryId);
        return standardServiceRepository.findAllByCategoryId(categoryId);
    }
}
