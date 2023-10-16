package com.bit.eduventure.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizUserHistoryRepository extends JpaRepository<QuizUserHistory, Integer> {


    Optional<QuizUserHistory> findByIdAndBoardNo(Integer id, Integer boardNo);



}
