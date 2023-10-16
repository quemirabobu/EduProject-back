package com.bit.eduventure.course.Repository;

import com.bit.eduventure.course.Entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    Optional<Course> findByClaName(String claName);
    List<Course> findByUserId(int userId);
    Page<Course> findAll(Specification<Course> spec, Pageable pageable);
}