package com.bit.eduventure.attendance.dto;

import com.bit.eduventure.attendance.entity.Attend;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendDTO {

    private int id;
    private LocalDateTime attStart;
    private LocalDateTime attFinish;
    private LocalDate attDate;
    private String attContent;
    private Boolean isCourse;
    private String userName;

    private int userNo;


    public AttendDTO(Attend attend) {
        this.id = attend.getId();
        this.userNo = attend.getUserNo();
        this.attStart = attend.getAttStart();
        this.attFinish = attend.getAttFinish();
        this.attDate = attend.getAttDate();
        this.attContent = attend.getAttContent();
        this.isCourse = attend.getIsCourse();
    }

    public Attend DTOToEntity() {
        Attend attend = Attend.builder()
                .id(this.id)
                .userNo(this.userNo)
                .attStart(this.attStart)
                .attFinish(this.attFinish)
                .attContent(this.attContent)
                .isCourse(this.isCourse)
                .build();
        return attend;
    }

}
