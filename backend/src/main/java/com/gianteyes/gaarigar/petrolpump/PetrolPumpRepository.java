package com.gianteyes.gaarigar.petrolpump;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PetrolPumpRepository extends JpaRepository<PetrolPumpModel, Long>, JpaSpecificationExecutor<PetrolPumpModel> {

    Optional<PetrolPumpModel> findByPhone(String Phone);

    @Query("select pp.id, pp.firstName, pp.address, count(pp.id) from PetrolPumpModel pp " +
            "join FuelDeliveryOrder fd on fd.petrolPump.id=pp.id " +
            "where fd.status=com.gianteyes.gaarigar.Order.OrderStatus.COMPLETED and fd.completedAt between ?1 and ?2 " +
            " group by pp.id, pp.firstName, pp.address " +
            "order by count(pp.id) DESC")
    List<Object> getMostActivePetrolPump(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT COUNT(p.id) from PetrolPumpModel p " +
            " where p.createdAt between ?1 and ?2")
    long getCountOfNewPetrolPumps(LocalDateTime startDate, LocalDateTime endDate);

    @Query("select pp.id, pp.firstName, pp.address, count(pp.id) from PetrolPumpModel pp " +
            "join FuelDeliveryOrder fd on fd.petrolPump.id=pp.id " +
            " where fd.status=com.gianteyes.gaarigar.Order.OrderStatus.CANCELLED and fd.cancelledAt between ?1 and ?2 " +
            "group by pp.id, pp.firstName, pp.address " +
            "order by count(pp.id) DESC ")
    List<Object> getMostCancelledPetrolPump(LocalDateTime startDate, LocalDateTime endDate);

}
