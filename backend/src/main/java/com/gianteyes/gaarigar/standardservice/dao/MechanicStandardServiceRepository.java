package com.gianteyes.gaarigar.standardservice.dao;

import com.gianteyes.gaarigar.standardservice.MechanicStandardServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MechanicStandardServiceRepository extends JpaRepository<MechanicStandardServiceModel, Long>,
        JpaSpecificationExecutor<MechanicStandardServiceModel> {


    @Query("select mss.standardService.id, mss.standardService.name , mss.price, count(mss.standardService.id) from MechanicStandardServiceModel mss " +
            "join StandardServiceOrder sso on sso.mechanicStandardService.id = mss.id " +
            "where sso.requestedAt between ?1 and ?2 " +
            "group by mss.standardService.id,mss.standardService.name, mss.price " +
            " ORDER BY count(mss.standardService.id) DESC")
    List<Object> getBestMechanicServices(LocalDateTime startDate, LocalDateTime endDate);


    List<MechanicStandardServiceModel> findByMechanicId(Long mechanicId);

    Optional<MechanicStandardServiceModel> findByMechanicIdAndStandardServiceId(Long mechanicId, Long standardServiceId);

}
