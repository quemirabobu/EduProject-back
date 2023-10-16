package com.bit.eduventure.vodBoard.repository;


import com.bit.eduventure.vodBoard.entity.VodBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VodBoardRepository extends JpaRepository<VodBoard, Integer> {
    Page<VodBoard> findAll(Specification<VodBoard> spec, Pageable pageable);
}
