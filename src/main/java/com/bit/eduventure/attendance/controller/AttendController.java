
package com.bit.eduventure.attendance.controller;

import com.bit.eduventure.User.Entity.CustomUserDetails;
import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.attendance.entity.Attend;
import com.bit.eduventure.attendance.service.AttendService;
import com.bit.eduventure.attendance.dto.AttendDTO;
import com.bit.eduventure.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendController {

    private final AttendService attendService;
    private final UserService userService;

    //가장 최초 화면: 처음 화면은 Null로 하고 수업일 여부를 띄워준다.
    @GetMapping("/main")
    public ResponseEntity<?> getAttendForUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ResponseDTO<AttendDTO> responseDTO = new ResponseDTO<>();
        int userId = customUserDetails.getUser().getId();
        User user = userService.findById(userId);

        if(user.getUserType().equals("parent")) {
            userId = user.getUserJoinId();
        }

        try {
            AttendDTO response = attendService.getIsCourseForUser(userId);
            response.setUserName(userService.findById(userId).getUserName());

            responseDTO.setItem(response);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 입실 처리
    @GetMapping("/enter")
    public ResponseEntity<?> registerEnterTime(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDTO<AttendDTO> responseDTO = new ResponseDTO<>();
        int userId = customUserDetails.getUser().getId();

        try {
            LocalDateTime attendTime = LocalDateTime.now(); // 현재 시간으로 입실 시간 설정

            AttendDTO response = attendService.registerAttendance(userId, attendTime);
            response.setUserName(userService.findById(userId).getUserName());

            responseDTO.setItem(response);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 퇴실 처리
    @GetMapping("/exit")
    public ResponseEntity<?> registerExitTime(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDTO<AttendDTO> responseDTO = new ResponseDTO<>();
        int userId = customUserDetails.getUser().getId();

        try {
            LocalDateTime exitTime = LocalDateTime.now(); // 현재 시간으로 퇴실 시간 설정

            AttendDTO response = attendService.registerExitTime(userId, exitTime);
            response.setUserName(userService.findById(userId).getUserName());

            responseDTO.setItem(response);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    // 특정 사용자의 출석 기록 조회
    @GetMapping("/attend")
    public ResponseEntity<?> getAttendanceRecordsByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDTO<AttendDTO> responseDTO = new ResponseDTO<>();
        int userId = customUserDetails.getUser().getId();
        User user = userService.findById(userId);
        if (user.getUserType().equals("parent")) {
            userId = user.getUserJoinId();
            user = userService.findById(userId);
        }
        try {
            List<AttendDTO> records = attendService.getAttendanceRecordsByUser(user);

            for(AttendDTO dto : records) {
                dto.setUserName(userService.findById(userId).getUserName());
            }


            responseDTO.setItems(records);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    // 특정 날짜의 특정 사용자 출석 기록 조회
    @GetMapping("/attend/date/{date}")
    public ResponseEntity<?> getAttendanceRecordsByUserAndDate(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        ResponseDTO<AttendDTO> responseDTO = new ResponseDTO<>();
        int userId = customUserDetails.getUser().getId();

        try {
            User user = userService.findById(userId);

            List<AttendDTO> records = attendService.getAttendanceRecordsByUserAndDate(user, date);
            for (AttendDTO dto : records) {
                dto.setUserName(userService.findById(userId).getUserName());
            }
            responseDTO.setItems(records);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    // 특정 달에 해당하는 특정 사용자 출석 기록 조회, 반환시에는 현재와 직전 month 반환해보기.
    @GetMapping("/attend/month/{yearMonth}")
    public ResponseEntity<?> getAttendanceRecordsByUserAndMonth(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        ResponseDTO<AttendDTO> responseDTO = new ResponseDTO<>();
        int userId = customUserDetails.getUser().getId();

        try {

            User user = userService.findById(userId);
            //현재 달의 값 반환
            List<AttendDTO> records = attendService.getAttendanceRecordsByUserAndMonth(user, yearMonth);
            //직전 달의 값 반환
            List<AttendDTO> records_prev = attendService.getAttendanceRecordsByUserAndMonth(user, yearMonth.minusMonths(1));

            //합쳐주겠다.
            for (AttendDTO record : records_prev) {
                records.add(record);
            }

            for (AttendDTO dto : records) {
                dto.setUserName(userService.findById(userId).getUserName());
            }

            responseDTO.setItems(records);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


    // 출석 기록 수정
    @PutMapping("/admin/attend")
    public ResponseEntity<?> updateAttendRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @RequestBody AttendDTO attendDTO) {
        ResponseDTO<AttendDTO> responseDTO = new ResponseDTO<>();

        try {
            Attend attend = attendDTO.DTOToEntity();
            Attend updatedAttend = attendService.updateAttendRecord(attend);
            AttendDTO updateAttendDTO = updatedAttend.EntityToDTO();

            responseDTO.setItem(updateAttendDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


    @DeleteMapping("/admin/attend")
    public ResponseEntity<?> deleteAttendanceRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                    @RequestBody String attList) {
        ResponseDTO<Map<String, Object>> responseDTO = new ResponseDTO<>();

        try {
            attendService.deleteAttendList(attList);

            Map<String, Object> returnMap = new HashMap<>();
            returnMap.put("msg", "정상적으로 삭제되었습니다.");
            responseDTO.setItem(returnMap);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage("Error deleting the record");
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}

