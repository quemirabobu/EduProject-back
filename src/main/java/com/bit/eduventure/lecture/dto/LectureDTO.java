package com.bit.eduventure.lecture.dto;

import com.bit.eduventure.lecture.entity.Lecture;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LectureDTO {

    private Integer id;
    private String title;
    private String liveStationId;
    private String liveThumb;
    private int couNo;
    private String teacher;
    private int userCount;


    public Lecture DTOTOEntity() {
        Lecture lecture = Lecture.builder()
                .id(this.id)
                .title(this.title)
                .liveStationId(this.liveStationId)
                .couNo(this.couNo)
                .build();

        return lecture;
    }
}
