package com.gianteyes.gaarigar.standardservice;

import com.gianteyes.gaarigar.exceptions.LocationNotAvailableException;
import com.gianteyes.gaarigar.exceptions.ResourceNotFoundException;
import com.gianteyes.gaarigar.standardservice.dao.MechanicStandardServiceRepository;
import com.gianteyes.gaarigar.standardservice.dao.MechanicStandardServiceSpecification;
import com.gianteyes.gaarigar.standardservice.dto.StandardServiceMapper;
import com.gianteyes.gaarigar.standardservice.dto.request.CreateMechanicStandardServiceDto;
import com.gianteyes.gaarigar.standardservice.dto.response.SearchMechanicStandardServiceResponseDto;
import com.gianteyes.gaarigar.user.UserModel;
import com.gianteyes.gaarigar.user.UserService;
import com.gianteyes.gaarigar.utils.GeoUtils;
import com.gianteyes.gaarigar.utils.SearchCriteria;
import com.gianteyes.gaarigar.utils.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class MechanicStandardServiceService {
    @Autowired
    private MechanicStandardServiceRepository mechanicStandardServiceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StandardServiceMapper standardServiceMapper;

    @Autowired
    private GeoUtils geoUtils;
    @Autowired
    private StandardServiceService standardServiceService;

    public MechanicStandardServiceModel createMechanicStandardService(CreateMechanicStandardServiceDto request) {
        MechanicStandardServiceModel mechanicStandardService = this.standardServiceMapper.mapCreateRequestToMechanicStandardServiceModel(request);
        mechanicStandardService.setId(null);
        mechanicStandardService.getMechanic().setImage(null);
        mechanicStandardService.setIsActive(true);
        return this.mechanicStandardServiceRepository.save(mechanicStandardService);
    }

    public Collection<MechanicStandardServiceModel> getAll() {
        return this.mechanicStandardServiceRepository.findAll();
    }

    public Collection<SearchMechanicStandardServiceResponseDto> getMechanicStandardServices(Long mechanicId) {
        return standardServiceMapper.mapMechanicStandardServiceModelToSearchResponse(this.mechanicStandardServiceRepository.findByMechanicId(mechanicId));
    }


    public Collection<SearchMechanicStandardServiceResponseDto> search(
            String searchTerm,
            int minPrice,
            int maxPrice,
            Long categoryId
    ) throws LocationNotAvailableException {
        /*
              This function searchs for mechanic standard services based on query params
              it returns all MechanicStandardServices within 5km radius of the user
              it is paginated, and sorted.
              it can be filtered by standard service category, standard service, price range, mechanic type
         */
        UserModel user = this.userService.getCurrentAuthenticatedUser();
        Specification spec = Specification.where(null);

        if (searchTerm != null) {
            spec = spec.and(MechanicStandardServiceSpecification.hasStandardServiceName(searchTerm));
        }
        MechanicStandardServiceSpecification mechanicStandardServiceSpecification = new MechanicStandardServiceSpecification();
        if (minPrice != 0) {
            mechanicStandardServiceSpecification.add(new SearchCriteria("price", minPrice, SearchOperation.GREATER_THAN_EQUAL));
        }
        if (maxPrice != 0) {
            mechanicStandardServiceSpecification.add(new SearchCriteria("price", maxPrice, SearchOperation.LESS_THAN_EQUAL));
        }
        mechanicStandardServiceSpecification.add(new SearchCriteria("isActive", true, SearchOperation.EQUAL));
        spec = spec.and(mechanicStandardServiceSpecification);
        if (categoryId != 0) {
            spec = spec.and(MechanicStandardServiceSpecification.hasCategoryId(categoryId));
        }
        Double[] coord = this.geoUtils.geometryToLatLong(user.getLocation());
        spec = spec.and(MechanicStandardServiceSpecification.filterWithinRadius(coord[0], coord[1], 5.0));
        List<MechanicStandardServiceModel> mechanicStandardServices = this.mechanicStandardServiceRepository.findAll(spec);
        List<SearchMechanicStandardServiceResponseDto> data = standardServiceMapper.mapMechanicStandardServiceModelToSearchResponse(mechanicStandardServices);
        return data;
    }

    public MechanicStandardServiceModel get(Long id) {
        return this.mechanicStandardServiceRepository.findById(id).orElse(null);
    }

    public List<Object> getBestMechanicServices(LocalDateTime start, LocalDateTime end) {
        return this.mechanicStandardServiceRepository.getBestMechanicServices(start, end);
    }

    public void toggleMechanicStandardService(Long id) {
        Optional<MechanicStandardServiceModel> ans = mechanicStandardServiceRepository.findById(id);
        if (ans.isEmpty())
            throw new ResourceNotFoundException("Standard Service Id: " + id + " not found");
        MechanicStandardServiceModel model = ans.get();
        model.setIsActive(!model.getIsActive());
        mechanicStandardServiceRepository.save(model);
    }
}
