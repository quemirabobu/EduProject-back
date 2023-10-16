package com.bit.eduventure.timetable.controller;

import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.User.Entity.CustomUserDetails;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.timetable.dto.TimeTableDTO;
import com.bit.eduventure.timetable.service.TimeTableService;
import com.bit.eduventure.validate.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/timetable")
@RequiredArgsConstructor
public class TimeTableController {

    private final TimeTableService timeTableService;
    private final UserService userService;
    private final ValidateService validateService;

    /* 시간표 등록 */
    @PostMapping("/regist")
    public ResponseEntity<?> registTimeTable(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                             @RequestBody TimeTableDTO requestDTO) {
        int userNo = customUserDetails.getUser().getId();
        validateService.validateTeacherAndAdmin(userService.findById(userNo));

        System.out.println("requestDTO: " + requestDTO);

        // 클라이언트에게 전달할 최종 응답 객체 생성
        ResponseDTO<String> response = new ResponseDTO<>();

        timeTableService.registerTimetable(requestDTO);  // 서비스 메서드 호출
        response.setItem("저장이 완료되었습니다."); // 응답 DTO 설정
        response.setStatusCode(HttpStatus.CREATED.value()); // 상태 코드 설정
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 성공적인 응답 반환
    }

    /* 시간표 목록 조회 */
    @GetMapping("/getTimeTable-list")
    public ResponseEntity<?> getAllTimetables() {
        ResponseDTO<TimeTableDTO> response = new ResponseDTO<>();

        System.out.println("시간표 컨트롤러 res111============");

            List<TimeTableDTO> res = timeTableService.getAllTimetables();

        System.out.println("res============"+res);
        response.setItems(res);
        response.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(response);
    }

    /* 시간표 삭제 */
    @DeleteMapping("/deleteTimeTable")
    public ResponseEntity<?> deleteTimeTable(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                             @RequestBody Map<String, String> request) {
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<Map<String, String>>();
        int userNo = customUserDetails.getUser().getId();
        validateService.validateTeacherAndAdmin(userService.findById(userNo));

        String claName = request.get("claName");
        String timeWeek = request.get("timeWeek");

        timeTableService.deleteTimetable(claName, timeWeek);
        Map<String, String> returnMap = new HashMap<String, String>();

        returnMap.put("msg", "정상적으로 삭제되었습니다.");

        responseDTO.setItem(returnMap);

        return ResponseEntity.ok().body(responseDTO);
    }

    /* 학생별 시간표 리스트 보기 */
    @GetMapping("/student/list")
    public ResponseEntity<?> getPaymentList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ResponseDTO<TimeTableDTO> response = new ResponseDTO<>();

        int userNo = Integer.parseInt(customUserDetails.getUsername());
        UserDTO userDTO = userService.findById(userNo).EntityToDTO();

        List<TimeTableDTO> timeTableDTOList= timeTableService.getTimetableByStudent(userDTO.getCourseDTO().getCouNo());

        // timeWeek의 첫 글자만 잘라서 저장.
        for (TimeTableDTO dto : timeTableDTOList) {
            String timeWeek = dto.getTimeWeek();
            if (timeWeek != null && !timeWeek.isEmpty()) {
                dto.setTimeWeek(timeWeek.substring(0, 1));
            }
        }


        response.setItems(timeTableDTOList);
        response.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(response);
    }

}
