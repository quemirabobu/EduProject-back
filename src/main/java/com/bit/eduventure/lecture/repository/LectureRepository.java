package com.bit.eduventure.lecture.repository;

import com.bit.eduventure.lecture.entity.Lecture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {
    Optional<Lecture> findByCouNo(int couNo);
    Optional<Lecture> findById(int lecId);
    Optional<Lecture> findByLiveStationId(String liveId);
    Page<Lecture> findAll(Specification<Lecture> spec, Pageable pageable);
}
