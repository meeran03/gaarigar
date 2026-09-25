package com.gianteyes.gaarigar.payment;

import com.gianteyes.gaarigar.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetailModel,Long> {
    Optional<PaymentDetailModel> findByTransactionId(String id);
    Optional<PaymentDetailModel> findByOrderModelId(Long id);
}
