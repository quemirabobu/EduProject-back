package com.bit.eduventure.vodBoard.dto;


import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.vodBoard.entity.VodBoard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VodBoardDTO { // 보드 dto에 원래 이름, 저장이름, 오브젝트이름
    private Integer id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private int hits;

    private String originPath;
    private String savePath;
    private String objectPath;

    private String originThumb;
    private String saveThumb;
    private String objectThumb;

    private int likeCount;
    private int likeStatus;

    private UserDTO userDTO;

    public VodBoard DTOTOEntity() {
        VodBoard vodBoard = VodBoard.builder()
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

                .user(this.userDTO.DTOToEntity())
                .build();
        return vodBoard;
    }
}
