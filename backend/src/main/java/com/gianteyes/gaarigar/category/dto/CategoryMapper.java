package com.gianteyes.gaarigar.category.dto;

import com.gianteyes.gaarigar.category.dto.request.CreateCategoryRequestDto;
import com.gianteyes.gaarigar.category.dto.response.CreateCategoryResponseDto;
import com.gianteyes.gaarigar.category.CategoryModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    @Autowired
    private ModelMapper modelMapper;

    public CategoryModel mapCreateCategoryRequestToCategory(CreateCategoryRequestDto createCategoryRequestDto) {
        return modelMapper.map(createCategoryRequestDto, CategoryModel.class);
    }

    public CreateCategoryResponseDto mapCategoryToCreateCategoryResponse(CategoryModel categoryModel) {
        return modelMapper.map(categoryModel, CreateCategoryResponseDto.class);
    }
}
