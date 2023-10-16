package com.bit.eduventure.payment.repository;

import com.bit.eduventure.payment.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Integer> {
    List<Receipt> findAllByPaymentId(int payId);
    void deleteAllByPaymentId(int payId);
}
