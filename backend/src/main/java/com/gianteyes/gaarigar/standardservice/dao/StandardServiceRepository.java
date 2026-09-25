package com.gianteyes.gaarigar.standardservice.dao;

import com.gianteyes.gaarigar.standardservice.StandardServiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StandardServiceRepository extends JpaRepository<StandardServiceModel, Long> {
    List<StandardServiceModel> findAllByCategoryId(Long id);

    @Query("select mss.standardService.id, mss.standardService.name , count(mss.standardService.id) from MechanicStandardServiceModel mss " +
            "join StandardServiceOrder sso on sso.mechanicStandardService.id = mss.id " +
            "where sso.requestedAt between ?1 and ?2 " +
            "group by mss.standardService.id,mss.standardService.name ORDER BY count(mss.standardService.id) DESC")
    List<Object> getMostRequestedStandardServices(LocalDateTime startDate, LocalDateTime endDate);
}
