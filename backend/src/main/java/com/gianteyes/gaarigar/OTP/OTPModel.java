package com.gianteyes.gaarigar.OTP;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;


@Data
@RedisHash("OTPModel")
@NoArgsConstructor
@Getter
@Setter
public class OTPModel implements Serializable {
    @Id
    @Pattern(regexp = "^\\+\\d{12}$", message = "Phone Number is Invalid")
    private String phoneNumber;
    @NotNull
    private String OTPMessage;

    public OTPModel(@JsonProperty("phoneNumber") String phoneNumber,@JsonProperty("OTPMessage") String OTPMessage)
    {
        this.phoneNumber=phoneNumber;
        this.OTPMessage=OTPMessage;
    }

}
