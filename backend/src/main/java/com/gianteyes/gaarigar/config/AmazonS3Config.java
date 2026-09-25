package com.gianteyes.gaarigar.config;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class AmazonS3Config {
    @Bean public AmazonS3 s3() {
        return AmazonS3ClientBuilder.standard().withRegion(System.getenv().getOrDefault("AWS_REGION", "us-east-1")).build();
    }
}
