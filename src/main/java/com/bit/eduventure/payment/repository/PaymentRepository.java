
package com.bit.eduventure.payment.repository;

import com.bit.eduventure.payment.entity.Payment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Transactional
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    //학생정보와 월로 데이터 찾기
    @Query("SELECT p FROM Payment p WHERE p.payTo = :payTo AND YEAR(p.issDate) = :year AND MONTH(p.issDate) = :month")
    List<Payment> findByPayToAndYearMonth(@Param("payTo") int payTo, @Param("year") int year, @Param("month") int month);

    //payTo(학생PK)로 해당하는 모든 결제정보 찾기
    List<Payment> findAllByPayTo(int payTo);
}

