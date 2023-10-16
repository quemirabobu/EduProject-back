package com.bit.eduventure.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="T_QUIZ_BOARD_FILE")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//boardNo 가 FK, PK
//boardFileNo 도 pk
//다중 Pk 매핑방식
//1. ID클래스
//2. embededId
//다중 pk일때 해당 엔티티에서 사용하는 ID 클래스를 지정.
@IdClass(QuizBoardFileId.class)
public class QuizBoardFile {

    @Id
    //3 관계지정 ; 일대일 다대일 일대다 등등
    @ManyToOne
    //2 Fk로 가져올 컬럼 지정
    @JoinColumn(name="BOARD_NO")
    @OnDelete(action = OnDeleteAction.CASCADE) // CASCADE 설정 추가

    //1 boardno를 가져올 객체를 맴버변수로 선언
    private QuizBoard quizBoard;
    @Id
    private int boardFileNo;
    private String boardFileName;
    private String boardFilePath;
    private String boardFileOrigin;
    private String boardFileCate;
    @Transient
    private String boardFileStatus;
    @Transient
    private String newFileNm;


    public QuizBoardFileDTO EntityToDTO() {

        QuizBoardFileDTO quizBoardFileDTO = QuizBoardFileDTO.builder().boardNo(this.quizBoard.getBoardNo()).boardFileNo(this.boardFileNo).boardFileName(this.boardFileName).boardFilePath(this.boardFilePath).boardFileOrigin(this.boardFileOrigin).boardFileCate(this.boardFileCate).build();

        return quizBoardFileDTO;
    }


}
