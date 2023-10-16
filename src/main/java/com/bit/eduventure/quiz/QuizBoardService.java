package com.bit.eduventure.quiz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuizBoardService {


    QuizBoard getBoard(int boardNo);

    List<QuizBoard> getBoardList();

    void insertBoard(QuizBoard board, List<QuizBoardFile> uploadFileList);

    void updateBoard(QuizBoard board, List<QuizBoardFile> uFileList);

    void deleteBoard(int boardNo);

    List<QuizBoardFile> getBoardFileList(int boardNo);

    Page<QuizBoard> getBoardList(Pageable pageable, String searchCondition, String searchKeyword);


    void plussGrossSample(int boardNo);

    void plussGrossRightAnswer(int boardNo);

    QuizBoardFile saveQuizFile(MultipartFile file);

    void deleteQuizFileList(int boardNo);

}
