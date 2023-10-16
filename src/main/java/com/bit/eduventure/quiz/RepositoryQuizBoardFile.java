package com.bit.eduventure.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositoryQuizBoardFile extends JpaRepository<QuizBoardFile, QuizBoardFileId> {




    @Query(value = "SELECT IFNULL(MAX(F.BOARD_FILE_NO),0)+1 FROM t_quiz_board_file F WHERE F.BOARD_NO= :boardNo", nativeQuery = true)
    public int findMaxFileNo(int boardNo);


    List<QuizBoardFile> findByQuizBoardBoardNo(@Param("boardNo") int boardNo);


//    List<BoardFile> findByBoardNo(int boardNo);





}
