package com.bit.eduventure.course.Entity;

import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.course.DTO.CourseDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
//@Table: 테이블 이름등을 지정
@Table(name="T_COURSE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    //반
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COU_NO")
    private Integer couNo;      //아이디값

    @Column(name = "COU_NAME", unique = true, nullable = false)
    private String claName;     //반 이름

    @Column(name = "COU_MEMO")
    private String couMemo;     //반 메모

    @ManyToOne
    @JoinColumn(name = "USER_NO")
    private User user;  //담당 선생님 유저 아이디

    public CourseDTO EntityToDTO(){
        CourseDTO courseDTO = CourseDTO.builder()
                .couNo(this.couNo)
                .claName(this.claName)
                .couMemo(this.couMemo)
                .userDTO(this.user.EntityToDTO())
                .build();
        return courseDTO;

    }
}