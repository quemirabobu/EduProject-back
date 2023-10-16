package com.bit.eduventure.quiz;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class QuizBoardDTO {


    private int boardNo;
    private String boardTitle;
    private String boardContent;
    private String boardWriter;
    private String boardRegdate;
    private int boardCnt;
    private String claName;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;
    private int grossSample;
    private int grossRightAnswer;
    public QuizBoard DTOToEntity() {
        return QuizBoard.builder()
                .boardNo(this.boardNo).claName(this.claName).option1(this.option1).option2(this.option2).option3(this.option3).option4(this.option4).answer(this.answer)
                .boardTitle(this.boardTitle)
                .boardContent(this.boardContent)
                .boardWriter(this.boardWriter).grossSample(this.grossSample).grossRightAnswer(this.grossRightAnswer)
                .boardRegdate(LocalDateTime.parse(this.boardRegdate))
                .boardCnt(this.boardCnt)
                .build();
    }


}
