package com.bit.eduventure.timetable.repository;

import com.bit.eduventure.timetable.entity.TimeTable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Integer> {


    List<TimeTable> findByClaNameAndTimeWeek(String claName, String timeWeek);

    List<TimeTable> findAllByCouNo(int couNo);

    List<TimeTable> findAllByClaName(String claName);

    void deleteAllByCouNo(int couNo);
}
