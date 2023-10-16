package com.bit.eduventure.attendance.repository;

import com.bit.eduventure.attendance.entity.Attend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendRepository extends JpaRepository<Attend, Integer> {
    Optional<Attend> findById(Integer id);
    List<Attend> findAllByUserNo(Integer userId);
    List<Attend> findByUserNoAndAttStartBetween(Integer userId, LocalDateTime start, LocalDateTime end);
    List<Attend> findAllByUserNoAndAttDate(Integer userId, LocalDate attDate);
    //특정한 달(month)의 출석 기록 조회
    List<Attend> findByUserNoAndAttDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);

    Attend findByUserNoAndAttRegDate(int userNo, LocalDate today);

    //스케줄링 처리를 위해 퇴실 안찍은 사람 조회
    List<Attend> findByAttFinishIsNull();

    List<Attend> findByAttFinishIsNullAndAttRegDate(LocalDate yesterDay);

}