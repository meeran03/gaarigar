package com.gianteyes.gaarigar.category;

import com.gianteyes.gaarigar.category.dto.CategoryMapper;
import com.gianteyes.gaarigar.category.dto.request.CreateCategoryRequestDto;
import com.gianteyes.gaarigar.category.dto.response.CreateCategoryResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    public CreateCategoryResponseDto createCategory(CreateCategoryRequestDto categoryModel) {
        CategoryModel category = categoryMapper.mapCreateCategoryRequestToCategory(categoryModel);
        categoryRepository.save(category);
        return categoryMapper.mapCategoryToCreateCategoryResponse(category);
    }

    public List<CategoryModel> getAllCategories() {
        return categoryRepository.findAll();
    }

    public CategoryModel getCategoryById(Long id) {
        Optional<CategoryModel> result = categoryRepository.findById(id);
        if(result.isPresent())
            return result.get();
        else
            return null;
    }

    public void updateCategory(CategoryModel categoryModel) {
        categoryRepository.save(categoryModel);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
