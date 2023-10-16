package com.bit.eduventure.vodBoard.entity;

import com.bit.eduventure.vodBoard.dto.VodBoardDTO;
import com.bit.eduventure.vodBoard.dto.VodBoardLikeDTO;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table (name = "T_VOD_BOARD_LIKE")
@Builder
public class VodBoardLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VOD_LIKE_NO")
    private int id; // 내이름은VodBoard 좋아요 인덱스 자동생성이죠

    @Column(name = "VOD_LIKE_USER_NO", nullable = false)
    private int userNo; // 사용자 인덱스 (누가 좋아요를 눌렀는지 식별

    @Column(name = "VOD_BOARD_NO", nullable = false)
    private int vodNo; // VodBoard 인덱스

    public VodBoardLikeDTO EntityTODTO() {
        return VodBoardLikeDTO.builder()
                .id(this.id)
                .userNo(this.userNo)
                .vodNo(this.vodNo)
                .build();
    }
}
