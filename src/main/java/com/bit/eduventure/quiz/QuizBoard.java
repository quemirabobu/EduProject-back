package com.bit.eduventure.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
//@Entity: 엔티티 클래스로 지정
@Entity
//@Table: 테이블 이름등을 지정
@Table(name="T_QUIZ_BOARD")
//@SequenceGenerator: 시퀀스 생성해주는 어노테이션,
//                    MySQL 시퀀스라는 문법이 존재하지 않아서 시퀀스 테이블이 생성
//name: 시퀀스 제네레이터 이름 지정
//sequenceName: 시퀀스 이름 지정
//initailValue: 시퀀스의 시작 값 설정
//allocationSize: 시퀀스의 증감값 설정
@SequenceGenerator(
        name="QuizBoardSeqGenerator",
        sequenceName = "T_QUIZ_BOARD_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizBoard {


    //컬럼 정의
    //@Id: PK로 지정
    @Id
    //키 값 지정 방식
    //@GeneratedValue: 생성된 값으로 키 값을 지정하는 방식
    //GenerationType속성
    //AUTO: 기본 값, 데이터베이스가 알아서 키 값을 할당
    //IDENTITY: AUTO_INCREMENT를 사용하여 키 값 할당
    //SEQUENCE: 시퀀스를 사용하여 키 값 할당. 항상 SequenceGenerator와 함께 사용
    //TABLE: 키 값으로 사용될 값들을 별도의 테이블로 생성하여 관리하는 방식
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "QuizBoardSeqGenerator"
    )
    private int boardNo;
    private String boardTitle;
    private String boardContent;
    private String boardWriter;
    private String claName;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;
    @Column(name = "gross_sample")
    private int grossSample;

    @Column(name = "gross_right_answer")
    private int grossRightAnswer;

    //기본값 설정
    private LocalDateTime boardRegdate = LocalDateTime.now();
    //@Column: 컬럼에 대한 특성들을 지정
    //name: 어느 컬럼과 매핑될것인지
    //nullable: null값 허용 여부
    //unique: UK로 지정
    @Column(name="BOARD_CNT", nullable = false)
    private int boardCnt = 0;
//hmmm
    //@Transient: 테이블의 컬럼으로는 생성되지 않고 객체에서만 사용가능한 멤버변수
    @Transient
    private String searchCondition;
    @Transient
    private String searchKeyword;


    public QuizBoardDTO EntityToDTO(){
        return QuizBoardDTO.builder().boardNo(this.boardNo).claName(this.claName).option1(this.option1).option2(this.option2).option3(this.option3).option4(this.option4).answer(this.answer). grossSample(this.grossSample).grossRightAnswer(this.grossRightAnswer). boardTitle(this.boardTitle).boardWriter(this.boardWriter).boardContent(this.getBoardContent()).boardRegdate(this.getBoardRegdate().toString()).boardCnt(this.getBoardCnt())    .build();
    }

}