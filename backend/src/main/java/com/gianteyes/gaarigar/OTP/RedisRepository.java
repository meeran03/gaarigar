package com.gianteyes.gaarigar.OTP;

import com.gianteyes.gaarigar.exceptions.InvalidOTPException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Repository
public class RedisRepository {
    public static final String HASH_KEY = "OTPModel";
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ModelMapper modelMapper;
    public OTPModel save(OTPModel otpModel,int timer) {
        redisTemplate.opsForHash().put(HASH_KEY, otpModel.getPhoneNumber(), otpModel);
        if(timer!=0) {
            redisTemplate.expire(HASH_KEY, timer, TimeUnit.MINUTES);
        }
        return otpModel;
    }
    public void delete(String phoneNumber)
    {
       Long result= redisTemplate.opsForHash().delete(HASH_KEY,phoneNumber);
    }
    public List<OTPModel> findAll(){
        return redisTemplate.opsForHash().values(HASH_KEY);
    }

    public OTPModel findOTPByPhone(String phoneNumber){


             Object ans= redisTemplate.opsForHash().get(HASH_KEY,phoneNumber);
             if(ans!=null){
             OTPModel otp =modelMapper.map(ans,OTPModel.class);
             return otp;
             }
             else {
                throw new InvalidOTPException("Invalid Phone Number",phoneNumber);
             }

    }
}
