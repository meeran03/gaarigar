package com.gianteyes.gaarigar.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {
    Optional<CustomerModel> findByPhone(String phone);

    // find customers with createdAt between startDate and endDate
    @Query("select c from CustomerModel c where c.createdAt >= ?1 and c.createdAt <= ?2")
    List<CustomerModel> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

}
