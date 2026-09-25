package com.gianteyes.gaarigar.mechanic;

import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.mechanic.dto.request.UpdateMechanicOpDto;
import com.gianteyes.gaarigar.mechanic.dto.request.UpdateMechanicRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mechanic")
public class MechanicController {

    @Autowired
    private MechanicService mechanicService;

    @PostMapping("/update-location")
    @org.springframework.security.access.prepost.PreAuthorize("@access.role('MECHANIC')")
    public ResponseEntity updateMechanicLocation(@Valid @RequestBody Location request) {
        try {
            return ResponseEntity.ok(mechanicService.updateLocation(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-availability/{value}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.role('MECHANIC')")
    public void updateMechanicAvailability(@PathVariable boolean value) {
        mechanicService.updateAvailability(value);
    }

    @GetMapping("/{id}")
    public UpdateMechanicRequestDto getDetails(@PathVariable Long id) {
        return this.mechanicService.getMechanicDetails(id);
    }

    @PatchMapping(value = "/update/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.sameUser(#id)")
    public ResponseEntity updateMechanicDetails(@Valid @ModelAttribute UpdateMechanicOpDto request, @Nullable @RequestParam(value = "image", required = false) MultipartFile file, @PathVariable Long id) {
        try {
            mechanicService.updateMechanicDetailsOptional(request, id, file);
            return ResponseEntity.ok("Updated Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
