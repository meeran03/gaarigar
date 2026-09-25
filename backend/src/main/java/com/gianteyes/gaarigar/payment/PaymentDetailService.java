package com.gianteyes.gaarigar.payment;

import com.gianteyes.gaarigar.exceptions.UnknownErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

import static java.time.LocalDateTime.now;

@Service
public class PaymentDetailService {

    @Autowired
    private PaymentDetailRepository paymentDetailRepository;

    public PaymentDetailModel createDetail(PaymentDetailModel paymentDetailModel) {
        return this.paymentDetailRepository.save(paymentDetailModel);
    }

    public PaymentDetailModel updateDetail(String id, String message) {
        Optional<PaymentDetailModel> result = paymentDetailRepository.findByTransactionId(id);
        if (result.isEmpty())
            throw new UnknownErrorException("An unknown error occurred");
        if (Objects.equals(message, "succeeded")) {
            result.get().setStatus(PaymentStatus.COMPLETED);
        } else if (Objects.equals(message, "cancelled")) {
            result.get().setStatus(PaymentStatus.CANCELLED);
        } else if (Objects.equals(message, "created")) {
            result.get().setStatus(PaymentStatus.INTENT_CAPTURED);
        } else if (Objects.equals(message, "failed")) {
            result.get().setStatus(PaymentStatus.FAILED);
        }
        result.get().setUpdatedAt(now(ZoneId.systemDefault()));
        return createDetail(result.get());
    }

    public Optional<PaymentDetailModel> findByOrderId(Long orderId) {
        return paymentDetailRepository.findByOrderModelId(orderId);
    }
}
