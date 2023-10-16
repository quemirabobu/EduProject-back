package com.bit.eduventure.vodBoard.dto;

import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.vodBoard.entity.VodBoardComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class VodBoardCommentDTO {

    private int id; // 댓글의 고유한 인덱스(ID)
    private String vodCmtContent; // 댓글 내용
    private LocalDateTime vodCmtRegdate; // 댓글 작성일자와 시간
    private int vodCmtParentNo; //대댓글을 위한 부모 댓글의 인덱스
    private int vodNo; // 댓글이 속하는 VOD 게시글의 인덱스(ID)
    private UserDTO userDTO; // 댓글 작성자의 회원 인덱스(ID)
    
    private List<VodBoardCommentDTO> vodSonCmtList;    //자식 댓글 리스트
    public VodBoardComment DTOTOEntity() {
        return VodBoardComment.builder()
                .id(this.id)
                .vodCmtContent(this.vodCmtContent)
                .vodCmtRegdate(this.vodCmtRegdate)
                .vodCmtParentNo(this.vodCmtParentNo)
                .vodNo(this.vodNo)
                .user(this.userDTO.DTOToEntity())
                .build();
    }
}
