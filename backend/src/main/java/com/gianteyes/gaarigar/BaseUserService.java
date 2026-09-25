package com.gianteyes.gaarigar;

import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.config.GeneralConfig;
import com.gianteyes.gaarigar.exceptions.AlreadyExistsException;
import com.gianteyes.gaarigar.exceptions.ResourceNotFoundException;
import com.gianteyes.gaarigar.user.UserModel;
import com.gianteyes.gaarigar.user.UserService;
import com.gianteyes.gaarigar.user.dto.UpdateLocationResponseDto;
import com.gianteyes.gaarigar.utils.FileUpload;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BaseUserService<T extends UserModel> {
    @Autowired
    private final JpaRepository<T, Long> userRepository;
    @Autowired
    private FileUpload fileUpload;
    @Autowired
    private GeneralConfig config;
    @Autowired
    private UserService userService;

    public BaseUserService(JpaRepository<T, Long> userRepository) {
        this.userRepository = userRepository;
    }

    public T createUser(T user) {
        if (userService.checkIfUserExists(user.getPhone())) {
            throw new AlreadyExistsException("User", "phone", user.getPhone());
        }
        String hashedPassword = userService.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public T decorateForUpdateUser(T user, Long userId) {
        Optional<T> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        T userToUpdate = userOptional.get();
        if (user.getPhone() != null) {
            userToUpdate.setPhone(user.getPhone());  //MAY ALREADY EXIST
        }
        if (user.getPassword() != null) {
            userToUpdate.setPassword(userService.hashPassword(user.getPassword()));
        }
        if (user.getLastName() != null) {
            userToUpdate.setLastName(user.getLastName());
        }
        if (user.getFirstName() != null) {
            userToUpdate.setFirstName(user.getFirstName());
        }
        if (user.getIsActive()) {
            userToUpdate.setIsActive(user.getIsActive());
        }
        if (user.getIsVerified()) {
            userToUpdate.setIsVerified(user.getIsVerified());
        }
        if (user.getFirstName() != null) {
            userToUpdate.setFirstName(user.getFirstName());
        }
        if (user.getImage() != null) {
            userToUpdate.setImage(user.getImage());
        }
        if (user.getLocation() != null) {
            userToUpdate.setLocation(user.getLocation());
        }
        return userToUpdate;
    }

    public T updateUser(T user) {
        return userRepository.save(user);
    }

    public T getByUserId(Long userId) {
        Optional<T> userOp = userRepository.findById(userId);
        if (userOp.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        T user = userOp.get();
        if (!Objects.equals(user.getImage(), "") && user.getImage() != null) {
            user.setImage(fileUpload.generateUrl(user.getImage()));
        }
        return user;
    }

    public UpdateLocationResponseDto updateLocation(Location request) throws ParseException {
        return this.userService.updateLocation(request.convertToString());
    }


    public List<T> getAll() {
        List<T> users = userRepository.findAll();
        users.forEach(user -> {

            if (!Objects.equals(user.getImage(), "") && user.getImage() != null) {
                user.setImage(fileUpload.generateUrl(user.getImage()));
            }
        });
        return users;
    }

    public String uploadProfileImage(MultipartFile image, String path) {
        try {
            return fileUpload.uploadImage(image, path);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file.", e);
        }

    }
}
