package com.gianteyes.gaarigar.standardservice;

import com.gianteyes.gaarigar.standardservice.dto.request.CreateStandardServiceRequestDTO;
import com.gianteyes.gaarigar.standardservice.dto.response.CreateStandardServiceResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Collection;

/*
    This is controller class for standard service
 */


@RestController
@RequestMapping("/api/standard-service")
public class StandardServiceController {

    @Autowired
    private StandardServiceService standardServiceService;

    @PostMapping(value = "/create", produces = "application/json")
    @RolesAllowed("ADMIN")
    public CreateStandardServiceResponseDTO createStandardService(@Valid @RequestBody CreateStandardServiceRequestDTO request) throws IOException {
        return standardServiceService.create(request);
    }

    @GetMapping(value = "/", produces = "application/json")
    @RolesAllowed({"CUSTOMER", "ADMIN", "MECHANIC"})
    public Collection<StandardServiceModel> getAllStandardServices() {
        return standardServiceService.getAll();
    }

    @GetMapping("/get/categoryId/{categoryId}")
    public Collection<StandardServiceModel> getStandardServiceByCategoryId(@PathVariable Long categoryId) {
        return standardServiceService.getStandardServiceByCategoryId(categoryId);
    }
}
