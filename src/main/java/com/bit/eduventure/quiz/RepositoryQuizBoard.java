package com.bit.eduventure.quiz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
@Repository
public interface RepositoryQuizBoard extends JpaRepository<QuizBoard, Integer> {


    Optional<QuizBoard> findByBoardNo(int boardNo);

    Page<QuizBoard> findByBoardTitleContainingOrBoardContentContainingOrBoardWriterContaining(String searchKeyword, String searchKeyword1, String searchKeyword2, Pageable pageable);

    Page<QuizBoard> findByBoardTitleContaining(String searchKeyword, Pageable pageable);

    Page<QuizBoard> findByBoardContentContaining(String searchKeyword, Pageable pageable);

    Page<QuizBoard> findByBoardWriterContaining(String searchKeyword, Pageable pageable);


    @Modifying
    @Transactional
    @Query("UPDATE QuizBoard q SET q.grossSample = q.grossSample + 1 WHERE q.boardNo = :boardNo")
    void plusGrossSample(@Param("boardNo") int boardNo);
    @Modifying
    @Transactional
    @Query("UPDATE QuizBoard q SET q.grossSample = q.grossSample + 1, q.grossRightAnswer = q.grossRightAnswer + 1 WHERE q.boardNo = :boardNo")
    void plusGrossSampleAndRightAnswer(int boardNo);



}
