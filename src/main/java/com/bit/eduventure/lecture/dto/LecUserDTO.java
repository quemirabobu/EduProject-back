package com.bit.eduventure.lecture.dto;

import com.bit.eduventure.lecture.entity.LecUser;
import lombok.Builder;

@Builder
public class LecUserDTO {
    int id;
    int lecturePK;
    int userPK;
    String userName;

    public LecUser DTOTOEntity() {
        return LecUser.builder()
                .id(this.id)
                .lecturePK(this.lecturePK)
                .userPK(this.userPK)
                .userName(this.userName)
                .build();
    }
}
