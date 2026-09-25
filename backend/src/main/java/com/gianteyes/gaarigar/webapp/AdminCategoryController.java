package com.gianteyes.gaarigar.webapp;

import com.gianteyes.gaarigar.category.CategoryModel;
import com.gianteyes.gaarigar.category.CategoryService;
import com.gianteyes.gaarigar.category.dto.request.CreateCategoryRequestDto;
import com.gianteyes.gaarigar.category.dto.response.CreateCategoryResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminCategoryController {
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/category")
    public String category(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories";
    }

    @RequestMapping("/category/edit/{id}")
    public String editCategory(@PathVariable("id") Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "editCategory";
    }

    @PostMapping("/category/update")
    public String updateCategory(CategoryModel categoryModel) {
        categoryService.updateCategory(categoryModel);
        return "redirect:/admin/category";
    }

    @RequestMapping("/category/add")
    public String addCategory(Model model) {
        model.addAttribute("category", new CreateCategoryRequestDto());
        return "addCategory";
    }

    @PostMapping("/category/create")
    public String createCategory(CreateCategoryRequestDto categoryModel) {
        CreateCategoryResponseDto category = categoryService.createCategory(categoryModel);
        return "redirect:/admin/category";
    }

}
