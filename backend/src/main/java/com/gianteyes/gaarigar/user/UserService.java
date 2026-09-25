package com.gianteyes.gaarigar.user;

import com.gianteyes.gaarigar.user.dto.UpdateLocationResponseDto;
import com.gianteyes.gaarigar.utils.GeoUtils;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/*
 * This class is used to implement the business logic for the user
 */
@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GeoUtils geoUtils;


    public UserModel createUser(UserModel user) {
        return userRepository.save(user);
    }


    public Optional<UserModel> getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }


    public Boolean checkIfUserExists(String phone) {
        return this.getUserByPhone(phone).isPresent();
    }


    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (checkIfUserExists(username)) {
            UserModel user = this.getUserByPhone(username).get();
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getUserType().toString()));
            return new org.springframework.security.core.userdetails.User(user.getPhone(), user.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public Boolean checkPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    public UpdateLocationResponseDto updateLocation(String location) throws ParseException {
        UserModel user = getCurrentAuthenticatedUser();
        user.setLocation(
                (Point) geoUtils.wktToGeometry(location)
        );
        userRepository.save(user);
        return new UpdateLocationResponseDto(
                "Location updated successfully",
                true
        );
    }

    public UserModel getCurrentAuthenticatedUser() {
        String phone = SecurityContextHolder.getContext().getAuthentication().getName();
        return this.getUserByPhone(phone).get();
    }

    public void update(UserModel user) {
        userRepository.save(user);
    }
}
