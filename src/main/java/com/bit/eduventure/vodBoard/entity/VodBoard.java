package com.bit.eduventure.vodBoard.entity;


import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.vodBoard.dto.VodBoardDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_VOD_BOARD")
public class VodBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VOD_NO")
    private Integer id;

    @Column(name = "VOD_TITLE", nullable = false)
    private String title;

    @Column(name = "VOD_CONTENT", nullable = false)
    private String content;

    @Column(name = "VOD_WRITER", nullable = false)
    private String writer;

    @Column(name = "VOD_REG", nullable = false)
    private LocalDateTime regDate;

    @Column(name = "VOD_MOD")
    private LocalDateTime modDate;

    @Column(name = "VOD_HITS", nullable = false) //조회수
    private int hits = 0;

    @Column(name = "VOD_SAVE_PATH") //실제로 저장된 파일명과 원본 파일명으로 나눠서 저장
    private String savePath;

    @Column(name = "VOD_ORIGIN_PATH")
    private String originPath;

    @Column(name = "VOD_OBJECT_PATH")
    private String objectPath;

    @Column(name = "VOD_SAVE_THUMB")
    private String saveThumb;

    @Column(name = "VOD_ORIGIN_THUMB")
    private String originThumb;

    @Column(name = "VOD_OBJECT_THUMB")
    private String objectThumb;

    @ManyToOne
    @JoinColumn(name = "USER_NO")
    private User user;  // 유저 정보를 findBy 안쓰고 편하게 쓰기 위해서 작성함.

    public VodBoardDTO EntityToDTO() {
        VodBoardDTO vodBoardDTO = VodBoardDTO.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .writer(this.writer)
                .regDate(this.regDate)
                .modDate(this.modDate)

                .savePath(this.savePath)
                .originPath(this.originPath)
                .objectPath(this.objectPath)

                .saveThumb(this.saveThumb)
                .originThumb(this.originThumb)
                .objectThumb(this.objectThumb)

                .hits(this.hits)

                .userDTO(this.user.EntityToDTO())
                .build();
        return vodBoardDTO;
    }
}
