package com.bit.eduventure.vodBoard.entity;


import com.bit.eduventure.vodBoard.dto.VodBoardFileDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_VOD_BOARD_FILE") // 첨부파일
public class VodBoardFile {
    @Id
    @Column(name = "VOD_FILE_NO") // 파일 식별자(파일의 고유번호) pk
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int vodFileNo;
    @Column(name = "VOD_ORIGIN_NAME")
    private String vodOriginName;
    @Column(name = "VOD_SAVE_NAME")
    private String vodSaveName;

    @Column(name = "VOD_NO") // 게시판 식별자 fk
    private int vodBoardNo;

    public VodBoardFileDTO EntityToDTO() {
        VodBoardFileDTO vodBoardFileDTO = VodBoardFileDTO.builder()
                .vodFileNo(this.vodFileNo)
                .vodOriginName(this.vodOriginName)
                .vodSaveName(this.vodSaveName)
                .vodBoardNo(this.vodBoardNo)
                .build();
        return vodBoardFileDTO;
    }
}
