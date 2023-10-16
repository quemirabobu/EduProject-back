package com.bit.eduventure.timetable.entity;

import com.bit.eduventure.timetable.dto.TimeTableDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
//@Table: 테이블 이름등을 지정
@Table(name="T_TIMETABLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIME_NO")
    private Integer timeNo; //아이디

    @Column(name = "TIME_TITLE")
    private String timeTitle;   //수업 제목

    @Column(name = "TIME_PLACE")
    private String timePlace;   //수업 장소

    @Column(name = "TIME_COLOR")
    private String timeColor;   //색상

    @Column(name = "TIME_CLASS")
    private String timeClass;   //수업 ??교시

    @Column(name = "TIME_WEEK")
    private String timeWeek;    //수업 ??요일

    @Column(name = "TIME_TEACHER")
    private String timeTeacher;

    @Column(name = "COU_NO")
    private int couNo;

    @Column(name = "COU_NAME")
    private String claName;      //반 조인 컬럼대체

    public TimeTableDTO EntityTODTO() {
        TimeTableDTO timeTableDTO = TimeTableDTO.builder()
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
        return timeTableDTO;
    }
}
