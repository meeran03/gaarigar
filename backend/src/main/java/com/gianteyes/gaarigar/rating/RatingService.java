package com.gianteyes.gaarigar.rating;

import com.gianteyes.gaarigar.BaseUserService;
import com.gianteyes.gaarigar.customer.CustomerModel;
import com.gianteyes.gaarigar.customer.CustomerService;
import com.gianteyes.gaarigar.exceptions.InvalidUserTypeException;
import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.mechanic.MechanicRepository;
import com.gianteyes.gaarigar.mechanic.MechanicService;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpModel;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpRepository;
import com.gianteyes.gaarigar.rating.dto.RatingRequestDto;
import com.gianteyes.gaarigar.user.UserModel;
import com.gianteyes.gaarigar.user.UserService;
import com.gianteyes.gaarigar.user.UserType;
import com.stripe.model.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingService {
    @Autowired
    private UserService userService;
    @Autowired
    private com.gianteyes.gaarigar.user.UserRepository userRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private MechanicService mechanicService;
    @Autowired
    private MechanicRepository mechanicRepository;
    @Autowired
    private PetrolPumpRepository petrolPumpRepository;
    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ModelMapper modelMapper;

    @org.springframework.security.access.prepost.PreAuthorize("@access.role('CUSTOMER') and @access.sameUser(#rateObj.ratedBy)")
    public void rate(RatingRequestDto rateObj) {
        Double newRating;

        CustomerModel customer = customerService.getCustomerById(rateObj.getRatedBy());

        UserModel user = userRepository.findById(rateObj.getRatedTo()).orElseThrow();
        long count = ratingRepository.countByRatedTo(user);
        if(user.getUserType() == UserType.MECHANIC) {
            Optional<MechanicModel> mechanic = mechanicService.getMechanicByPhone(user.getPhone());
            newRating = (mechanic.get().getRating() + rateObj.getRating())/(count + 1);
            mechanic.get().setRating(newRating);
            mechanicRepository.save(mechanic.get());
        }
        else if(user.getUserType() == UserType.PETROL_PUMP)
        {
            Optional<PetrolPumpModel> petrolPumpModel = petrolPumpRepository.findByPhone(user.getPhone());
             newRating = (petrolPumpModel.get().getRating() + rateObj.getRating())/(count + 1);
            petrolPumpModel.get().setRating(newRating);
            petrolPumpRepository.save(petrolPumpModel.get());
        }
        else {
            throw new InvalidUserTypeException( user.getUserType());
        }
        RatingModel rating =  modelMapper.map(rateObj, RatingModel.class);
        rating.setRatedBy(customer);
        rating.setRatedTo(user);
        ratingRepository.save(rating);
    }
}
