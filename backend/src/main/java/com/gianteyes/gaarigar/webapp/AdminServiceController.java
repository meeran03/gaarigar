package com.gianteyes.gaarigar.webapp;

import com.gianteyes.gaarigar.category.CategoryService;
import com.gianteyes.gaarigar.standardservice.StandardServiceService;
import com.gianteyes.gaarigar.standardservice.dto.request.CreateStandardServiceRequestDTO;
import com.gianteyes.gaarigar.standardservice.dto.request.UpdateStandardServiceRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminServiceController {
    @Autowired
    private StandardServiceService standardService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/standard-service")
    public String category(Model model) {
        model.addAttribute("standardServices", standardService.getAll());
        return "standardServices";
    }

    @RequestMapping("/standard-service/edit/{id}")
    public String editStandardService(@PathVariable("id") Long id, Model model) {
        model.addAttribute("standardService", standardService.getDecoratedForUpdate(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "editStandardService";
    }

    @PostMapping("/standard-service/update")
    public String updateStandardService(UpdateStandardServiceRequestDto obj) throws IOException {
        standardService.update(obj);
        return "redirect:/admin/standard-service";
    }

    @RequestMapping("/standard-service/add")
    public String addStandardService(Model model) {
        model.addAttribute("standardService", CreateStandardServiceRequestDTO.builder().build());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "addStandardService";
    }

    @PostMapping("/standard-service/create")
    public String createCategory(CreateStandardServiceRequestDTO obj) throws IOException {
        standardService.create(obj);
        return "redirect:/admin/standard-service";
    }

}
