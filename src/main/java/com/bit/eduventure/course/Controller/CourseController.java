package com.bit.eduventure.course.Controller;

import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.User.Entity.CustomUserDetails;
import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.course.DTO.CourseDTO;
import com.bit.eduventure.course.Entity.Course;
import com.bit.eduventure.course.Service.CourseService;
import com.bit.eduventure.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @PostMapping("/getcourse")
    public ResponseEntity<?> getcourse(@RequestBody CourseDTO courseDTO) {
        ResponseDTO<CourseDTO> responseDTO = new ResponseDTO<>();
        System.out.println(courseDTO);
        System.out.println(courseDTO.getCouNo());
        System.out.println("/////////////////");
        Course course = courseService.getCourse(courseDTO.getCouNo());

        CourseDTO courseDTOtosend = course.EntityToDTO();
        responseDTO.setItem(courseDTOtosend);
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }

    //반 학생 수를 포함한 반 리스트
    @GetMapping("/course-list")
    public ResponseEntity<?> getCourseList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDTO<CourseDTO> responseDTO = new ResponseDTO<>();

        List<Course> courseList = courseService.getCourseList();

        List<CourseDTO> courseDTOList = courseList.stream()
                .map(course -> {
                    CourseDTO courseDTO = course.EntityToDTO();
                    long studentCnt = userService.getUserCountCourse(course.getCouNo());
                    courseDTO.setStudentCnt(studentCnt);
                    return courseDTO;
                })
                .collect(Collectors.toList());

        responseDTO.setItems(courseDTOList);
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/course/{teacherId}")
    public ResponseEntity<?> getCourse(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                       @PathVariable int teacherId) {
        ResponseDTO<CourseDTO> responseDTO = new ResponseDTO<>();
        List<Course> courseList = courseService.findByTeacherId(teacherId);
        List<CourseDTO> courseDTOList = courseList.stream()
                .map(Course::EntityToDTO)
                .collect(Collectors.toList());

        responseDTO.setItems(courseDTOList);
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/course")
    public ResponseEntity<?> creatCourse(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestBody CourseDTO courseDTO) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();

        int userNo = courseDTO.getTeacherId();
        UserDTO userDTO = userService.findById(userNo).EntityToDTO();
        courseDTO.setUserDTO(userDTO);

        courseService.createCourse(courseDTO);

        responseDTO.setItem("반 생성 완료");
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/course/delete")
    public ResponseEntity<?> deleteCourseList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @RequestBody String couNoList) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();

        List<Integer> integerList = courseService.jsonToIntList(couNoList);

        courseService.deleteCourseList(integerList);

        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/course/{couNo}")
    public ResponseEntity<?> modifyCourse(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                          @PathVariable int couNo,
                                          @RequestBody CourseDTO courseDTO) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();
        Course course = courseService.getCourse(couNo);
        int teacherId = courseDTO.getTeacherId();
        if (teacherId != 0) {
            User user = userService.findById(teacherId);
            course.setUser(user);
        }

        course.setClaName(courseDTO.getClaName());
        course.setCouMemo(courseDTO.getCouMemo());
        courseService.createCourse(course.EntityToDTO());

        responseDTO.setItem("반 수정 완료");
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/page/course-list")
    public ResponseEntity<?> getCourseList(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                          @RequestParam(value = "category", required = false, defaultValue = "all") String category,
                                          @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ResponseDTO<CourseDTO> responseDTO = new ResponseDTO<>();

        Page<Course> pageBoard = courseService.getCourseList(page, category, keyword);

        Page<CourseDTO> pageList = new PageImpl<>(
                pageBoard.get().map(course -> {
                    CourseDTO courseDTO = course.EntityToDTO();
                    long studentCnt = userService.getUserCountCourse(course.getCouNo());
                    courseDTO.setStudentCnt(studentCnt);
                    return courseDTO;
                }).collect(Collectors.toList()),
                pageBoard.getPageable(),
                pageBoard.getTotalElements()
        );


        responseDTO.setPageItems(pageList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);

    }
}
