package com.gianteyes.gaarigarapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordOTPModel {
    private String newToken;
    private String phoneNumber;
    private String newPassword;
}
