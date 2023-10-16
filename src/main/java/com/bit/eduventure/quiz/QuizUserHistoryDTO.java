package com.bit.eduventure.quiz;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class QuizUserHistoryDTO {

    private Integer no;
    private Integer boardNo;
    private Integer id;


    public QuizUserHistory DTOToEntity() {
        return QuizUserHistory.builder()
                .boardNo(this.boardNo).pkNo(this.no).id(this.id)
                .build();
    }


}
