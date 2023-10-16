package com.bit.eduventure.postIt.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostItDTO {
    private Long id; // 포스트잇의 아이디
    private Integer senderId; // 보내는 사람의 ID
    private Integer receiverId; // 받는 사람의 ID
    private String message; // 포스트잇 메시지
    private LocalDateTime sentDate; // 보낸 날짜
    private boolean replied; // 응답 여부
}
