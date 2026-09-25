package com.gianteyes.gaarigar.OTP;

import com.auth0.jwt.algorithms.Algorithm;
import com.gianteyes.gaarigar.OTP.dto.request.ChangePasswordRequestDto;
import com.gianteyes.gaarigar.OTP.dto.response.SendOTPResponseDto;
import com.gianteyes.gaarigar.OTP.dto.response.VerifyAndUpdateResponseDto;
import com.gianteyes.gaarigar.exceptions.AlreadyExistsException;
import com.gianteyes.gaarigar.exceptions.InvalidOTPException;
import com.gianteyes.gaarigar.exceptions.ResourceNotFoundException;
import com.gianteyes.gaarigar.user.UserModel;
import com.gianteyes.gaarigar.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

import static java.time.LocalDateTime.now;

@Service
public class OTPService {
    @org.springframework.beans.factory.annotation.Value("${jwt.secret}")
    private String jwtSecret;


    @Autowired
    private UserService userService;

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private TwilioSMSSender twilioSMSSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public SendOTPResponseDto sendOTPService(String phone) {
        SendOTPResponseDto response = new SendOTPResponseDto();
        if (this.userService.checkIfUserExists(phone)) {
            UserModel user = this.userService.getUserByPhone(phone).get();
            //create random OTP of 6 digits
            Random random = new Random();
            int number = random.nextInt(999999);
            String otp = String.format("%06d", number);

            //create and save OTP in redis
            OTPModel otpRequest = new OTPModel();
            otpRequest.setOTPMessage(otp);
            otpRequest.setPhoneNumber(phone);
            System.out.println(otp);
            OTPModel save = this.redisRepository.save(otpRequest, 3);
            //send sms to the user
            this.twilioSMSSender.sendSms(save, "Your Code For Verification : ");
            response.setSuccessMessage("OTP Send Successfully to Your Phone.");
            LocalDateTime expireTime = now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES).plusMinutes(3);
            DateTimeFormatter FormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            response.setExpireDate(expireTime.format(FormatObj));

        } else {
            throw new ResourceNotFoundException("User","phone", phone);
        }
       //AlreadyExistsException("User", "phone", user.getPhone())
        return response;
    }

    public VerifyAndUpdateResponseDto verifyOTPRequest(OTPModel otp) {
        VerifyAndUpdateResponseDto response = new VerifyAndUpdateResponseDto();
        String phoneNo = otp.getPhoneNumber();
        String otpCode = otp.getOTPMessage();
        Optional<UserModel> user = this.userService.getUserByPhone(phoneNo);
        if (!user.isPresent()) {
            throw new ResourceNotFoundException("User","Phone", phoneNo);
        }
        OTPModel dataRetrieved = redisRepository.findOTPByPhone(phoneNo);

        if (dataRetrieved != null) {
            if (dataRetrieved.getOTPMessage().equals(otpCode)) {
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
                String accessToken = com.auth0.jwt.JWT.create()
                        .withSubject(phoneNo)
                        .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer("auth0")
                        .withClaim("role", user.get().getUserType().toString())
                        .sign(algorithm);
                //update the value against phoneNumber
                redisRepository.delete(phoneNo);
                OTPModel otpRequest = new OTPModel();
                otpRequest.setOTPMessage(accessToken);
                otpRequest.setPhoneNumber(phoneNo);
                OTPModel save = this.redisRepository.save(otpRequest, 0);
                response.setSuccessMessage(accessToken);
            } else {
                throw new InvalidOTPException("Invalid Code For Forgot Password.");
            }
        } else {
            throw new InvalidOTPException("Invalid Request For Code Verification.");
        }
        return response;
    }

    protected VerifyAndUpdateResponseDto updateForgotPassword(ChangePasswordRequestDto changePasswordRequestDto) {
        VerifyAndUpdateResponseDto response = new VerifyAndUpdateResponseDto();
        String phoneNumber = changePasswordRequestDto.getPhoneNumber();
        String token = changePasswordRequestDto.getNewToken();
        OTPModel dataRetrieved = redisRepository.findOTPByPhone(phoneNumber);
        if (dataRetrieved != null) {
            if (dataRetrieved.getOTPMessage().equals(token)) {
                //change the password of the user
                UserModel user = this.userService.getUserByPhone(phoneNumber).get();
                String requestedPassword = changePasswordRequestDto.getNewPassword();
                String hashPassword = passwordEncoder.encode(requestedPassword);
                user.setPassword(hashPassword);
                this.userService.update(user);
                //update user profile
                response.setSuccessMessage("Password Successfully Updated");
            } else {
                throw new InvalidOTPException("Invalid Phone Number",phoneNumber);
            }
        } else {
            throw new InvalidOTPException("Invalid Phone Number");
        }
        return response;
    }

}
