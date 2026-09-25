package com.gianteyes.gaarigar.category;

import com.gianteyes.gaarigar.category.dto.request.CreateCategoryRequestDto;
import com.gianteyes.gaarigar.category.dto.response.CreateCategoryResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed("ADMIN")
    public CreateCategoryResponseDto createCategory(@RequestBody CreateCategoryRequestDto createCategoryRequestDto) {
        return categoryService.createCategory(createCategoryRequestDto);
    }

    @GetMapping(value = "/")
    public List<CategoryModel> getAllCategories() {
        return categoryService.getAllCategories();
    }

}
