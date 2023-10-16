package com.bit.eduventure.quiz;

import jakarta.persistence.*;
import lombok.*;

@Entity
//@Table: 테이블 이름등을 지정
@Table(name="T_QUIZ_USER_HISTORY")
//@SequenceGenerator: 시퀀스 생성해주는 어노테이션,
//                    MySQL 시퀀스라는 문법이 존재하지 않아서 시퀀스 테이블이 생성
//name: 시퀀스 제네레이터 이름 지정
//sequenceName: 시퀀스 이름 지정
//initailValue: 시퀀스의 시작 값 설정
//allocationSize: 시퀀스의 증감값 설정
@SequenceGenerator(
        name="QuizUserHistory",
        sequenceName = "T_QUIZ_User_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class QuizUserHistory {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer pkNo;
    private Integer boardNo;
    @Column(name = "USER_NO")
    private Integer id;

    public QuizUserHistoryDTO EntityToDTO(){
        return QuizUserHistoryDTO.builder().boardNo(this.boardNo).no(this.pkNo).id(this.id).build();
    }


}
