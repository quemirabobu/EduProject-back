package com.bit.eduventure.course.DTO;


import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.course.Entity.Course;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {

    private Integer couNo;
    private String claName;
    private String couMemo;
    private int teacherId;
    private long studentCnt;
    private UserDTO userDTO;

    public Course DTOToEntity(){
        Course course = Course.builder()
                .couNo(this.couNo)
                .claName(this.claName)
                .couMemo(this.couMemo)
                .user(this.userDTO.DTOToEntity())
                .build();
        return course;
    }



}
