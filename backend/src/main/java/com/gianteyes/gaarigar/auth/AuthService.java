package com.gianteyes.gaarigar.auth;

import com.auth0.jwt.algorithms.Algorithm;
import com.gianteyes.gaarigar.auth.dto.AuthMapper;
import com.gianteyes.gaarigar.auth.dto.request.*;
import com.gianteyes.gaarigar.auth.dto.response.LoginResponseDto;
import com.gianteyes.gaarigar.auth.dto.response.RegisterMechanicResponseDto;
import com.gianteyes.gaarigar.auth.dto.response.RegisterPetrolPumpResponseDto;
import com.gianteyes.gaarigar.auth.dto.response.RegisterResponseDto;
import com.gianteyes.gaarigar.common.admin.AdminModel;
import com.gianteyes.gaarigar.common.admin.AdminService;
import com.gianteyes.gaarigar.customer.CustomerModel;
import com.gianteyes.gaarigar.customer.CustomerService;
import com.gianteyes.gaarigar.exceptions.ApiRequestException;
import com.gianteyes.gaarigar.exceptions.ResourceNotFoundException;
import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.mechanic.MechanicService;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpModel;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpService;
import com.gianteyes.gaarigar.user.UserModel;
import com.gianteyes.gaarigar.user.UserService;
import com.gianteyes.gaarigar.user.UserType;
import com.gianteyes.gaarigar.utils.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthService {
    @org.springframework.beans.factory.annotation.Value("${jwt.secret}")
    private String jwtSecret;

    /*  this function tries to login user, if successful, maps User to LoginResponseDto and returns it
        else raises relevant exception
    */
    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MechanicService mechanicService;
    @Autowired
    private AdminService adminService;

    @Autowired
    private PetrolPumpService petrolPumpService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FileUpload fileUpload;


    LoginResponseDto login(LoginRequestDto request) {
        if (userService.checkIfUserExists(request.getPhone())) {
            UserModel user = userService.getUserByPhone(request.getPhone()).get();
            if (Boolean.TRUE.equals(user.getIsActive()) && userService.checkPassword(request.getPassword(), user.getPassword())) {
                LoginResponseDto responseDto = authMapper.mapUserToLoginResponseDto(user);
                if (!Objects.equals(user.getImage(), "") && user.getImage() != null) {
                    responseDto.setImage(fileUpload.generateUrl(user.getImage()));
                }
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
                String accessToken = com.auth0.jwt.JWT.create()
                        .withSubject(user.getPhone())
                        .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 10 * 600 * 1000))
                        .withIssuer("auth0")
                        .withClaim("role", user.getUserType().toString())
                        .sign(algorithm);
                String refreshToken = com.auth0.jwt.JWT.create()
                        .withSubject(user.getPhone())
                        .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 30 * 600 * 1000))
                        .withIssuer("auth0")
                        .sign(algorithm);
                responseDto.setAccessToken(accessToken);
                responseDto.setRefreshToken(refreshToken);
                if (request.getFcmToken() != null) {
                    user.setFcmToken(request.getFcmToken());
                    userService.update(user);
                }
                return responseDto;
            } else {
                throw new ResourceNotFoundException("Invalid password");
            }
        } else {
           // throw new ApiRequestException("User does not exist");
            throw new ResourceNotFoundException("User","phone" , request.getPhone());
        }
    }

    RegisterResponseDto registerCustomer(RegisterCustomerRequestDto request) {
        CustomerModel userData = authMapper.mapRegisterRequestToToCustomerModel(request);
        userData.setCreatedAt(LocalDateTime.now());
        if (userData.getEmail() != null)
            userData.setEmailNotifications(true);
        CustomerModel savedUser = null;
        userData.setUserType(UserType.CUSTOMER);
        savedUser = customerService.createUser(userData);
        return authMapper.mapCustomerModelToRegisterResponseDto(savedUser);

    }

    RegisterResponseDto registerAdmin(RegisterRequestDto request) {
        AdminModel userData = authMapper.mapRegisterRequestToToAdminModel(request);
        userData.setCreatedAt(LocalDateTime.now());
        userData.setEmailNotifications(false);
        AdminModel savedUser = null;
        userData.setUserType(UserType.ADMIN);
        savedUser = adminService.createUser(userData);
        return authMapper.mapAdminModelToRegisterResponseDto(savedUser);
    }

    RegisterMechanicResponseDto registerMechanic(RegisterMechanicRequestDto request) {
        MechanicModel userData = authMapper.mapRegisterRequestToToMechanicModel(request);
        userData.setCreatedAt(LocalDateTime.now());
        userData.setEmailNotifications(false);
        MechanicModel savedUser = null;
        userData.setUserType(UserType.MECHANIC);
        userData.setIsAvailable(true);
        savedUser = mechanicService.createUser(userData);
        return authMapper.mapMechanicModelToRegisterResponseDto(savedUser);
    }

    RegisterPetrolPumpResponseDto registerPetrolPump(RegisterPetrolPumpRequestDto request) {
        PetrolPumpModel userData = authMapper.mapRegisterRequestToToPetrolPumpModel(request);
        userData.setCreatedAt(LocalDateTime.now());
        userData.setEmailNotifications(false);
        PetrolPumpModel savedUser = null;
        userData.setUserType(UserType.PETROL_PUMP);
        userData.setIsAvailable(true);
        savedUser = petrolPumpService.createUser(userData);
        return authMapper.mapPetrolPumpModelToRegisterResponseDto(savedUser);
    }
}
