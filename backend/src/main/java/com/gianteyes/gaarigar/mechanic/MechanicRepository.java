package com.gianteyes.gaarigar.mechanic;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MechanicRepository extends JpaRepository<MechanicModel, Long>, JpaSpecificationExecutor<MechanicModel> {
    Optional<MechanicModel> findByPhone(String phone);


    //    find first 10 mechanics by order of their rating
    @Query("select m from MechanicModel m order by m.rating desc")
    List<MechanicModel> findTop10Mechanics();

    @Query(value = "SELECT COUNT(m) from MechanicModel m where m.createdAt between ?1 and ?2")
    long getCountOfNewMechanics(LocalDateTime startDate, LocalDateTime endDate);

    @Query("select m.id, m.firstName, count(m.id) from MechanicModel m " +
            "join MechanicOrderModel mo on mo.mechanic.id=m.id " +
            " where mo.requestedAt between ?1 and ?2 " +
            "group by m.id, m.firstName, m.lastName " +
            "order by count(m.id) DESC ")
    List<Object> getMostCancelledMechanics(LocalDateTime startDate, LocalDateTime endDate);


}
