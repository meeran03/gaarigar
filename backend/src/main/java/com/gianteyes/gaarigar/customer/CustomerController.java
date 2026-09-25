package com.gianteyes.gaarigar.customer;


import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.customer.dto.request.UpdateCustomerOpDto;
import com.gianteyes.gaarigar.customer.dto.request.UpdateCustomerRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.sameUser(#id)")
    public UpdateCustomerRequestDto getCustomerDetails(@PathVariable Long id) {
        return this.customerService.getCustomerDetails(id);
    }
    @PatchMapping(value = "/update/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.sameUser(#id)")
    public ResponseEntity updateCustomerDetails(@Valid @ModelAttribute UpdateCustomerOpDto request, @RequestParam("image") MultipartFile file, @PathVariable Long id) {
        try {
            return ResponseEntity.ok(customerService.updateCustomerDetailsOptional(request, id, file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/update-location")
    @org.springframework.security.access.prepost.PreAuthorize("@access.role('CUSTOMER')")
    public ResponseEntity updateCustomerLocation(@Valid @RequestBody Location request) {
        try {
            return ResponseEntity.ok(customerService.updateLocation(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
