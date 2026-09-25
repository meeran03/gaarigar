package com.gianteyes.gaarigar.petrolpump;

import com.gianteyes.gaarigar.BaseUserService;
import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.config.GeneralConfig;
import com.gianteyes.gaarigar.customer.dto.request.UpdateCustomerOpDto;
import com.gianteyes.gaarigar.exceptions.LocationNotAvailableException;
import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.mechanic.MechanicRepository;
import com.gianteyes.gaarigar.mechanic.MechanicType;
import com.gianteyes.gaarigar.mechanic.dao.MechanicSpecification;
import com.gianteyes.gaarigar.mechanic.dto.request.UpdateMechanicRequestDto;
import com.gianteyes.gaarigar.petrolpump.dao.PetrolPumpSpecification;
import com.gianteyes.gaarigar.petrolpump.dto.PetrolPumpMapper;
import com.gianteyes.gaarigar.petrolpump.dto.request.UpdatePetrolPumpOpDto;
import com.gianteyes.gaarigar.petrolpump.dto.request.UpdatePetrolPumpRequestDto;
import com.gianteyes.gaarigar.user.UserModel;
import com.gianteyes.gaarigar.user.UserService;
import com.gianteyes.gaarigar.utils.FileUpload;
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

@Service
public class PetrolPumpService extends BaseUserService<PetrolPumpModel> {

    @Autowired
    PetrolPumpMapper petrolPumpMapper;
    @Autowired
    GeneralConfig config;

    @Autowired
    private FileUpload fileUpload;

    @Autowired
    public PetrolPumpService(PetrolPumpRepository petrolPumpRepository) {
        super(petrolPumpRepository);
    }

    @Autowired
    public PetrolPumpRepository petrolPumpRepository;
    @Autowired
    public UserService userService;
    public PetrolPumpModel updatePetrolPumpDetailsOptional(UpdatePetrolPumpOpDto customer, Long customerId, MultipartFile file) {
        PetrolPumpModel petrolPumpModel = petrolPumpRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (Objects.nonNull(file)) {
            petrolPumpModel.setImage(this.uploadProfileImage(file, config.CUSTOMER_IMAGES_PATH));
        }
        if (Objects.nonNull(customer.getFirstName())) {
            petrolPumpModel.setFirstName(customer.getFirstName());
        }
        if (Objects.nonNull(customer.getLastName())) {
            petrolPumpModel.setLastName(customer.getLastName());
        }
        if (Objects.nonNull(customer.getPassword())) {
            petrolPumpModel.setPassword(customer.getPassword());
        } else {
            petrolPumpModel.setPassword(null);
        }
        petrolPumpModel = this.decorateForUpdateUser(petrolPumpModel, customerId);
        if (Objects.nonNull(customer.getAddress())) {
            petrolPumpModel.setAddress(customer.getAddress());
        }
        return this.updateUser(petrolPumpModel);
    }
    public UpdatePetrolPumpRequestDto getPetrolPumpDetails(Long id) {
        PetrolPumpModel mechanic = this.getByUserId(id);
        return this.petrolPumpMapper.convertToDto(mechanic);
    }
    public PetrolPumpModel updatePetrolPumpDetails(UpdatePetrolPumpRequestDto petrolPump, Long userId) throws ParseException {
        if (!Objects.equals(petrolPump.getImageFile().getOriginalFilename(), "")) {
            petrolPump.setImage(this.uploadProfileImage(petrolPump.getImageFile(), config.PETROL_PUMP_IMAGES_PATH));
        }
        if (!Objects.equals(petrolPump.getPassword(), petrolPump.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }
        PetrolPumpModel petrolPumpModel = this.petrolPumpMapper.convertToEntity(petrolPump);
        PetrolPumpModel updatedOriginal = this.decorateForUpdateUser(petrolPumpModel, userId);

        if (petrolPumpModel.getRating() != null) {
            updatedOriginal.setRating(petrolPumpModel.getRating());
        }
        if (petrolPumpModel.getIsAvailable() != null) {
            updatedOriginal.setIsAvailable(petrolPumpModel.getIsAvailable());
        }
        if (petrolPumpModel.getAddress() != null) {
            updatedOriginal.setAddress(petrolPumpModel.getAddress());
        }
        return this.updateUser(updatedOriginal);
    }
    public List<PetrolPumpModel> searchNearbyPetrolPump( Location obj) throws LocationNotAvailableException {

        Specification spec = Specification.where(null);
        PetrolPumpSpecification petrolPumpSpecification = new PetrolPumpSpecification();
        petrolPumpSpecification.add(new SearchCriteria("isAvailable", true, SearchOperation.EQUAL));
        spec = spec.and(petrolPumpSpecification);
        spec = spec.and(PetrolPumpSpecification.getWithinDistance(obj.getLongitude(), obj.getLatitude(), 5.0));
        List<PetrolPumpModel> petrolPumps = this.petrolPumpRepository.findAll(spec);
        petrolPumps.forEach(mechanic -> {
            if (!Objects.equals(mechanic.getImage(), "") && mechanic.getImage() != null) {
                mechanic.setImage(fileUpload.generateUrl(mechanic.getImage()));
            }
        });
        return petrolPumps;
    }
    public Long getCountOfNewPetrolPumps(LocalDateTime start, LocalDateTime end) {
        return this.petrolPumpRepository.getCountOfNewPetrolPumps(start, end);
    }
    public List<Object> getMostCancelledPetrolPumps(LocalDateTime start, LocalDateTime end) {
        return this.petrolPumpRepository.getMostCancelledPetrolPump(start, end);
    }
    public List<Object> getMostActivePetrolPumps(LocalDateTime start, LocalDateTime end) {
        return this.petrolPumpRepository.getMostActivePetrolPump(start, end);
    }
    public UpdatePetrolPumpRequestDto updateAvailability(boolean value) {
        UserModel user = userService.getCurrentAuthenticatedUser();
        PetrolPumpModel mechanic = petrolPumpRepository.findById(user.getId()).get();
        mechanic.setIsAvailable(value);
         PetrolPumpModel updated = petrolPumpRepository.save(mechanic);
        return this.petrolPumpMapper.convertToDto(updated);
    }
}
