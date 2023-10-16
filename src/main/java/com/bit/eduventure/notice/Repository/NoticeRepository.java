package com.bit.eduventure.notice.Repository;

import com.bit.eduventure.notice.Entity.Notice;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Transactional
public interface NoticeRepository extends JpaRepository<Notice,Integer> {
    @Query("SELECT n FROM Notice n WHERE n.claName = :claName OR n.claName = 'all'")
    List<Notice> findAllByClaAndAdmin(@Param("claName") String claName);
}
