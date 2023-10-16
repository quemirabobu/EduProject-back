package com.bit.eduventure.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class QuizUserHistoryService {

    private QuizUserHistoryRepository quizUserHistoryRepository;

    //생성자 주입
    @Autowired
    public QuizUserHistoryService(
            QuizUserHistoryRepository quizUserHistoryRepository
      ) {
   this.quizUserHistoryRepository = quizUserHistoryRepository;
    }

    public QuizUserHistory register(QuizUserHistory quizUserHistory) {
        QuizUserHistory quizUserHistory1 = quizUserHistoryRepository.findByIdAndBoardNo(quizUserHistory.getId(), quizUserHistory.getBoardNo()).orElse(null);
        if(quizUserHistory1==null){
            return quizUserHistoryRepository.save(quizUserHistory);

        }else{
            return null;
        }


    }

    public QuizUserHistory findIfExist(QuizUserHistory quizUserHistory) {
        return quizUserHistoryRepository.findByIdAndBoardNo(quizUserHistory.getId(), quizUserHistory.getBoardNo())
                .orElse(null);
    }
}
