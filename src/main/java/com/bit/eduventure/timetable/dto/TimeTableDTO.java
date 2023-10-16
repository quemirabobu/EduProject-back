package com.bit.eduventure.timetable.dto;

import com.bit.eduventure.timetable.entity.TimeTable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeTableDTO {
    private Integer timeNo;
    private String timeTitle;
    private String timePlace;
    private String timeClass; //교시
    private String timeColor;
    private String timeWeek;
    private String timeTeacher;
    private int couNo;
    private String claName;

    public TimeTable DTOTOEntity() {
        TimeTable timeTable = TimeTable.builder()
                .timeNo(this.timeNo)
                .timeTitle(this.timeTitle)
                .timePlace(this.timePlace)
                .timeColor(this.timeColor)
                .timeWeek(this.timeWeek)
                .timeClass(this.timeClass)
                .timeTeacher(this.timeTeacher)
                .couNo(this.couNo)
                .claName(this.claName)
                .build();
        return timeTable;
    }
}
