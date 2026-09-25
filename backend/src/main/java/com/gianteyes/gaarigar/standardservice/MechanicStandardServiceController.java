package com.gianteyes.gaarigar.standardservice;

import com.gianteyes.gaarigar.standardservice.dto.request.CreateMechanicStandardServiceDto;
import com.gianteyes.gaarigar.standardservice.dto.response.SearchMechanicStandardServiceResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/api/mechanic-standard-service")
public class MechanicStandardServiceController {
    @Autowired
    private MechanicStandardServiceService mechanicStandardServiceService;

    @PostMapping("/create")
    @org.springframework.security.access.prepost.PreAuthorize("@access.role('MECHANIC') and @access.sameUser(#request.mechanicId)")
    public void create(@Valid @RequestBody CreateMechanicStandardServiceDto request) {
        this.mechanicStandardServiceService.createMechanicStandardService(request);
    }

    @GetMapping("/{id}")
    public Collection<SearchMechanicStandardServiceResponseDto> getMechanicStandardServices(@PathVariable(value = "id") Long mechanicId) {
        return this.mechanicStandardServiceService.getMechanicStandardServices(mechanicId);
    }

    @DeleteMapping("/toggle/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.offering(#id)")
    public void removeMechanicStandardService(@PathVariable Long id) {
        mechanicStandardServiceService.toggleMechanicStandardService(id);
    }

    // this function search for mechanic standard services based on query params
    @GetMapping("/search")
    public Collection<SearchMechanicStandardServiceResponseDto> search(
            @RequestParam(value = "q", required = false) String searchTerm,
            @RequestParam(value = "min-price", required = false, defaultValue = "0") int minPrice,
            @RequestParam(value = "max-price", required = false, defaultValue = "0") int maxPrice,
            @RequestParam(value = "category", required = false, defaultValue = "0") Long categoryId
    ) {
        return this.mechanicStandardServiceService.search(searchTerm, minPrice, maxPrice, categoryId);
    }
}
