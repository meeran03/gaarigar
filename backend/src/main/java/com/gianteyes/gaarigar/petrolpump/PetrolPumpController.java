package com.gianteyes.gaarigar.petrolpump;

import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.customer.dto.request.UpdateCustomerOpDto;
import com.gianteyes.gaarigar.mechanic.dto.request.UpdateMechanicRequestDto;
import com.gianteyes.gaarigar.petrolpump.dto.request.UpdatePetrolPumpOpDto;
import com.gianteyes.gaarigar.petrolpump.dto.request.UpdatePetrolPumpRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/petrol-pump")
public class PetrolPumpController {

    @Autowired
    PetrolPumpService petrolPumpService;
    @PostMapping("/update-location")
    @org.springframework.security.access.prepost.PreAuthorize("@access.role('PETROL_PUMP')")
    public ResponseEntity updatePetrolPumpLocation(@Valid @RequestBody Location request) {
        try {
            return ResponseEntity.ok(petrolPumpService.updateLocation(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/update-availability/{value}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.role('PETROL_PUMP')")
    public UpdatePetrolPumpRequestDto updatePetrolPumpAvailability(@PathVariable boolean value) {
        return petrolPumpService.updateAvailability(value);
    }
    @GetMapping("/{id}")
    public UpdatePetrolPumpRequestDto getDetails(@PathVariable Long id)
    {
        return this.petrolPumpService.getPetrolPumpDetails(id);
    }
    @PatchMapping(value = "/update/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.sameUser(#id)")
    public ResponseEntity updatePetrolPumpDetails(@Valid @ModelAttribute UpdatePetrolPumpOpDto request, @RequestParam("image") MultipartFile file, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(petrolPumpService.updatePetrolPumpDetailsOptional(request, id, file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
