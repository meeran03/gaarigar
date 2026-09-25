package com.gianteyes.gaarigar.mechanic;

import com.gianteyes.gaarigar.BaseUserService;
import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.config.GeneralConfig;
import com.gianteyes.gaarigar.exceptions.LocationNotAvailableException;
import com.gianteyes.gaarigar.mechanic.dao.MechanicSpecification;
import com.gianteyes.gaarigar.mechanic.dto.MechanicMapper;
import com.gianteyes.gaarigar.mechanic.dto.request.UpdateMechanicOpDto;
import com.gianteyes.gaarigar.mechanic.dto.request.UpdateMechanicRequestDto;
import com.gianteyes.gaarigar.user.UserModel;
import com.gianteyes.gaarigar.user.UserService;
import com.gianteyes.gaarigar.utils.FileUpload;
import com.gianteyes.gaarigar.utils.GeoUtils;
import com.gianteyes.gaarigar.utils.SearchCriteria;
import com.gianteyes.gaarigar.utils.SearchOperation;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MechanicService extends BaseUserService<MechanicModel> {
    @Autowired
    public MechanicRepository mechanicRepository;
    @Autowired
    GeneralConfig config;
    @Autowired
    private UserService userService;
    @Autowired
    private GeoUtils geoUtils;
    @Autowired
    private MechanicMapper mechanicMapper;
    @Autowired
    private FileUpload fileUpload;

    @Autowired
    public MechanicService(MechanicRepository mechanicRepository) {
        super(mechanicRepository);
    }

    public List<MechanicModel> searchNearbyMechanic(MechanicType type, Location obj) throws LocationNotAvailableException {

        Specification spec = Specification.where(null);
        MechanicSpecification mechanicSpecification = new MechanicSpecification();
        mechanicSpecification.add(new SearchCriteria("isAvailable", true, SearchOperation.EQUAL));
        mechanicSpecification.add(new SearchCriteria("type", type, SearchOperation.EQUAL));
        spec = spec.and(mechanicSpecification);
        spec = spec.and(MechanicSpecification.getWithinDistance(obj.getLongitude(), obj.getLatitude(), 5.0));
        List<MechanicModel> mechanics = this.mechanicRepository.findAll(spec);
        mechanics.forEach(mechanic -> {
            if (!Objects.equals(mechanic.getImage(), "") && mechanic.getImage() != null) {
                mechanic.setImage(fileUpload.generateUrl(mechanic.getImage()));
            }
        });
        return mechanics;
    }

    public void updateMechanicDetailsOptional(UpdateMechanicOpDto mechanic, Long mechanicId, MultipartFile file) {
        MechanicModel mechanicModel = mechanicRepository.findById(mechanicId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (Objects.nonNull(file)) {
            mechanicModel.setImage(this.uploadProfileImage(file, config.MECHANIC_IMAGES_PATH));
        }
        if (Objects.nonNull(mechanic.getFirstName())) {
            mechanicModel.setFirstName(mechanic.getFirstName());
        }
        if (Objects.nonNull(mechanic.getLastName())) {
            mechanicModel.setLastName(mechanic.getLastName());
        }
        if (Objects.nonNull(mechanic.getPassword())) {
            mechanicModel.setPassword(mechanic.getPassword());
        } else {
            mechanicModel.setPassword(null);
        }
        if (Objects.nonNull(mechanic.getType())) {
            mechanicModel.setType(mechanic.getType());
        }
        mechanicModel = this.decorateForUpdateUser(mechanicModel, mechanicId);
        if (Objects.nonNull(mechanic.getType())) {
            mechanicModel.setType(mechanic.getType());
        }
        this.updateUser(mechanicModel);
    }

    public UpdateMechanicRequestDto getMechanicDetails(Long id) {
        MechanicModel mechanic = this.getByUserId(id);
        return this.mechanicMapper.convertToDto(mechanic);
    }

    public MechanicModel updateMechanicDetails(UpdateMechanicRequestDto mechanic, Long userId) throws ParseException {
        if (!Objects.equals(mechanic.getImageFile().getOriginalFilename(), "")) {
            mechanic.setImage(this.uploadProfileImage(mechanic.getImageFile(), config.MECHANIC_IMAGES_PATH));
        }
        if (!Objects.equals(mechanic.getPassword(), mechanic.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }
        MechanicModel mechanicModel = this.mechanicMapper.convertToEntity(mechanic);
        MechanicModel updatedOriginal = this.decorateForUpdateUser(mechanicModel, userId);

        if (mechanicModel.getRating() != null) {
            updatedOriginal.setRating(mechanicModel.getRating());
        }
        if (mechanicModel.getIsAvailable() != null) {
            updatedOriginal.setIsAvailable(mechanicModel.getIsAvailable());
        }
        if (mechanicModel.getType() != null) {
            updatedOriginal.setType(mechanicModel.getType());
        }
        return this.updateUser(updatedOriginal);
    }

    public List<MechanicModel> getTopRatedMechanics() {
        List<MechanicModel> results = this.mechanicRepository.findTop10Mechanics();
        results.forEach(user -> {
            if (!Objects.equals(user.getImage(), "") && user.getImage() != null) {
                user.setImage(fileUpload.generateUrl(user.getImage()));
            }
        });
        return results;
    }

    public Long getCountOfNewMechanics(LocalDateTime start, LocalDateTime end) {
        return this.mechanicRepository.getCountOfNewMechanics(start, end);
    }

    public List<Object> getMostCancelledMechanics(LocalDateTime start, LocalDateTime end) {
        return this.mechanicRepository.getMostCancelledMechanics(start, end);
    }

    public Optional<MechanicModel> getMechanicByPhone(String phone) {
        return this.mechanicRepository.findByPhone(phone);
    }

    public void updateAvailability(boolean value) {
        UserModel user = userService.getCurrentAuthenticatedUser();
        MechanicModel mechanic = mechanicRepository.findById(user.getId()).get();
        mechanic.setIsAvailable(value);
        mechanicRepository.save(mechanic);
    }
}
