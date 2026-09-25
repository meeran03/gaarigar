package com.gianteyes.gaarigar.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "User", indexes = {
        @Index(name = "idx_usermodel_id", columnList = "id"),

}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_phone", columnNames = {"phone"})
})
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="stripeId",nullable=true)
    private String stripeId;
    @Column(name = "phone", nullable = false)
    @Pattern(regexp = "^\\+\\d{12}$", message = "Phone number is invalid")
    private String phone;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;


    @Column(name = "password", nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "userType", nullable = false)
    private UserType userType;

    @Column(name = "isVerified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "isActive")
    private Boolean isActive = true;

    private Point location;
    @Column(columnDefinition = "TEXT")
    private String fcmToken;
    private LocalDateTime createdAt;

    private Boolean emailNotifications = false;

    public UserModel(UserModel user) {
        this.id = user.getId();
        this.phone = user.getPhone();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.password = user.getPassword();
        this.userType = user.getUserType();
        this.isVerified = user.getIsVerified();
        this.image = user.getImage();
        this.isActive = user.getIsActive();
        this.location = user.getLocation();
        this.fcmToken = user.getFcmToken();
        this.createdAt = user.getCreatedAt();
    }

    public String convertLocationForGoogleMapsUrl() {
        if (location == null) {
            return "";
        }
        return location.getY() + "," + location.getX();
    }

    String getFormattedName() {
        return firstName + " " + lastName;
    }
}
