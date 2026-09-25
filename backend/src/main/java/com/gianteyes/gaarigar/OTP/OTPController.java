package com.gianteyes.gaarigar.OTP;


import com.gianteyes.gaarigar.OTP.dto.request.ChangePasswordRequestDto;
import com.gianteyes.gaarigar.OTP.dto.request.OTPRequestDto;
import com.gianteyes.gaarigar.OTP.dto.response.SendOTPResponseDto;
import com.gianteyes.gaarigar.OTP.dto.response.VerifyAndUpdateResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sms")
public class OTPController {
    @Autowired
    private OTPService otpService;

    @PostMapping("/send")
    public SendOTPResponseDto sendOTP(@RequestBody OTPRequestDto request) {
        return otpService.sendOTPService(request.getPhoneNumber());
    }

    @PostMapping("/verify")
    public VerifyAndUpdateResponseDto verifyOTP(@RequestBody @Valid OTPModel otp) {
        return otpService.verifyOTPRequest(otp);
    }

    @PostMapping("/change-password")
    protected VerifyAndUpdateResponseDto changePassword(@RequestBody @Valid ChangePasswordRequestDto changePasswordRequestDto) {
        return otpService.updateForgotPassword(changePasswordRequestDto);
    }


}
