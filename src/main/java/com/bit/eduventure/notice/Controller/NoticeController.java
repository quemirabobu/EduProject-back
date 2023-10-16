package com.bit.eduventure.notice.Controller;


import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.User.Entity.CustomUserDetails;
import com.bit.eduventure.notice.DTO.NoticeDTO;
import com.bit.eduventure.notice.Entity.Notice;
import com.bit.eduventure.notice.Service.NoticeService;
import com.bit.eduventure.validate.ValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final UserService userService;
    private final ValidateService validateService;


    @GetMapping("/notice-list")
    public ResponseEntity<?> getBoardList(
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails
                                         ) {
        ResponseDTO<NoticeDTO> responseDTO = new ResponseDTO<>();

        List<Notice> noticeList = noticeService.getNoticeList();

        List<NoticeDTO> noticeDTOList = noticeList.stream()
                .map(Notice::EntityToDTO)
                .collect(Collectors.toList());

        responseDTO.setItems(noticeDTOList);
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);

    }




    @PutMapping("/noticeupdate")
    public ResponseEntity<?> update(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    @RequestBody NoticeDTO noticeDTO) {
        ResponseDTO<NoticeDTO> responseDTO = new ResponseDTO<>();
        System.out.println(noticeDTO);
        System.out.println("notice dto 업데이트에 들어왔음");
        try {
            int userNo = customUserDetails.getUser().getId();
            User user = userService.findById(userNo);
            validateService.validateTeacherAndAdmin(user);

            Notice updateNotice = noticeService.getNotice(noticeDTO.getNoticeNo());

            updateNotice.setUser(user);
            updateNotice.setNoticeTitle(noticeDTO.getNoticeTitle());
            updateNotice.setNoticeContent(noticeDTO.getNoticeContent());
            updateNotice.setDate(noticeDTO.getDate());
            updateNotice.setClaName(noticeDTO.getClaName());

            updateNotice = noticeService.update(updateNotice);

            NoticeDTO returNotice = updateNotice.EntityToDTO();

            responseDTO.setItem(returNotice);
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }






    @GetMapping("/getnotice/{noticeNo}")
    public ResponseEntity<?> getNotice(@AuthenticationPrincipal CustomUserDetails userDetails,
                                       @PathVariable int noticeNo) {
        ResponseDTO<NoticeDTO> responseDTO = new ResponseDTO<>();

        Notice notice = noticeService.getNotice(noticeNo);

        NoticeDTO noticeDTOtosend = notice.EntityToDTO();

        responseDTO.setItem(noticeDTOtosend);
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/createnotice")
    public ResponseEntity<?> join(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @RequestBody NoticeDTO noticeDTO) {
        ResponseDTO<NoticeDTO> responseDTO = new ResponseDTO<>();
        System.out.println(noticeDTO);

        int userId = userDetails.getUser().getId();
        User user = userService.findById(userId);
        Notice notice = noticeDTO.DTOToEntity();
        notice.setUser(user);

        Notice resultNotice = noticeService.create(notice);

        NoticeDTO resultNoticeDTO = resultNotice.EntityToDTO();

        responseDTO.setItem(resultNoticeDTO);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotice(@PathVariable int id) {
        ResponseDTO<Map<String, String>> responseDTO = new ResponseDTO<Map<String, String>>();

        noticeService.deleteNotice(id);

        Map<String, String> returnMap = new HashMap<String, String>();

        returnMap.put("msg", "정상적으로 삭제되었습니다.");

        responseDTO.setItem(returnMap);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/course")
    public ResponseEntity<?> getCourseNotice(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ResponseDTO<NoticeDTO> responseDTO = new ResponseDTO<>();

        int userNo = userDetails.getUser().getId();
        User user = userService.findById(userNo);

        List<Notice> noticeList = noticeService.getCourseNoticeList(user.getCourse().getClaName());

        List<NoticeDTO> noticeDTOList = noticeList.stream()
                .map(Notice::EntityToDTO)
                .collect(Collectors.toList());

        responseDTO.setItems(noticeDTOList);
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }



}
