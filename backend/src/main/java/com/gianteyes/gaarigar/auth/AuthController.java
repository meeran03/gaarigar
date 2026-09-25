package com.gianteyes.gaarigar.auth;

import com.gianteyes.gaarigar.auth.dto.request.*;
import com.gianteyes.gaarigar.auth.dto.response.LoginResponseDto;
import com.gianteyes.gaarigar.auth.dto.response.RegisterMechanicResponseDto;
import com.gianteyes.gaarigar.auth.dto.response.RegisterPetrolPumpResponseDto;
import com.gianteyes.gaarigar.auth.dto.response.RegisterResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        return new ResponseEntity(authService.login(request), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/register/customer", produces = "application/json")
    public RegisterResponseDto registerCustomer(@Valid @RequestBody RegisterCustomerRequestDto request) {
        return authService.registerCustomer(request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/register/admin", produces = "application/json")
    public RegisterResponseDto registerAdmin(@Valid @RequestBody RegisterRequestDto request) {
        return authService.registerAdmin(request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/register/mechanic", produces = "application/json")
    public RegisterMechanicResponseDto register(@Valid @RequestBody RegisterMechanicRequestDto request) {
        return authService.registerMechanic(request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/register/petrolpump", produces = "application/json")
    public RegisterPetrolPumpResponseDto register(@Valid @RequestBody RegisterPetrolPumpRequestDto request) {
        return authService.registerPetrolPump(request);
    }
}
