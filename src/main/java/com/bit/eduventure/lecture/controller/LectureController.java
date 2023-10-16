package com.bit.eduventure.lecture.controller;


import com.bit.eduventure.User.Entity.CustomUserDetails;
import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.course.Entity.Course;
import com.bit.eduventure.course.Service.CourseService;
import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.lecture.dto.LectureDTO;
import com.bit.eduventure.lecture.entity.Lecture;
import com.bit.eduventure.lecture.service.LecUserService;
import com.bit.eduventure.lecture.service.LectureService;
import com.bit.eduventure.livestation.dto.LiveStationInfoDTO;
import com.bit.eduventure.livestation.dto.LiveStationUrlDTO;
import com.bit.eduventure.livestation.dto.RecordVodDTO;
import com.bit.eduventure.livestation.service.LiveStationService;
import com.bit.eduventure.objectStorage.service.ObjectStorageService;
import com.bit.eduventure.validate.ValidateService;
import com.bit.eduventure.vodBoard.entity.VodBoard;
import com.bit.eduventure.vodBoard.entity.VodBoardFile;
import com.bit.eduventure.vodBoard.service.VodBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequestMapping("/lecture")
@RequiredArgsConstructor
@RestController
public class LectureController {

    private final LectureService lectureService;
    private final LiveStationService liveStationService;
    private final UserService userService;
    private final CourseService courseService;
    private final VodBoardService vodBoardService;
    private final ObjectStorageService objectStorageService;
    private final ValidateService validateService;
    private final LecUserService lecUserService;

    //강사가 강의 개설
    @PostMapping("/lecture")
    public ResponseEntity<?> createLecture(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @RequestBody LectureDTO lectureDTO) {
        ResponseDTO<LiveStationInfoDTO> responseDTO = new ResponseDTO<>();

        //권한 확인
        int userNo = customUserDetails.getUser().getId();
        User user = userService.findById(userNo);
        validateService.validateTeacherAndAdmin(user);

        if (!StringUtils.hasText(lectureDTO.getTitle())) {
            throw new NullPointerException();
        }

        String title = lectureDTO.getTitle();

        String channelId = liveStationService.createChannel(title);

        lectureDTO.setLiveStationId(channelId);

        lectureDTO = lectureService.createLecture(lectureDTO).EntityTODTO();

        LiveStationInfoDTO liveStationInfoDTO = liveStationService.getChannelInfo(channelId);
        liveStationInfoDTO.setLectureId(lectureDTO.getId());

        responseDTO.setItem(liveStationInfoDTO);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);

    }

    @GetMapping("/lecture/{liveStationId}")
    public ResponseEntity<?> getLiveStation(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable String liveStationId) {
        ResponseDTO<LiveStationInfoDTO> response = new ResponseDTO<>();

        Lecture lecture = lectureService.getLectureLiveStationId(liveStationId);

        LiveStationInfoDTO liveStationInfoDTO = liveStationService.getChannelInfo(liveStationId);
        liveStationInfoDTO.setLectureId(lecture.getId());

        response.setItem(liveStationInfoDTO);
        response.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/lecture/{liveStationId}")
    public ResponseEntity<?> deleteLiveStation(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                               @PathVariable String liveStationId) {
        ResponseDTO<String> response = new ResponseDTO<>();

        //권한 확인
        int userNo = customUserDetails.getUser().getId();
        User user = userService.findById(userNo);
        validateService.validateTeacherAndAdmin(user);

        Lecture lecture = lectureService.getLectureLiveStationId(liveStationId);
        int lectureId = lecture.getId();

        RecordVodDTO recordVodDTO = liveStationService.getRecord(liveStationId);

        //녹화된 파일이 있다면 게시글 작성
        if (recordVodDTO != null) {
            Course course = courseService.getCourse(lecture.getCouNo());

            String vodName = recordVodDTO.getFileName();    //녹화된 파일명
            String thumb = "edu-venture.png";               //기본 썸네일

            //삭제 전 녹화파일 게시글 작성
            VodBoard vodBoard = VodBoard.builder()
                    .title(lecture.getTitle())
                    .content(lecture.getTitle() + " 으로 자동 생성된 게시글 입니다.")
                    .writer(course.getUser().getUserName())
                    .savePath(objectStorageService.getObjectSrc(vodName)) //영상 주소
                    .originPath(vodName)
                    .objectPath(vodName) //오브젝트에 저장된 영상 파일 명
                    .saveThumb(objectStorageService.getObjectSrc(thumb))
                    .objectThumb(thumb)
                    .user(course.getUser())
                    .build();
            List<VodBoardFile> fileList = new ArrayList<>();
            vodBoardService.insertBoard(vodBoard, fileList);
        }

        liveStationService.deleteChannel(liveStationId);
        lectureService.deleteLecture(lectureId);

        if (recordVodDTO != null) {
            response.setItem("녹화된 강의가 게시되었습니다.");
        } else {
            response.setItem("녹화된 강의가 없어 게시글 없이 삭제되었습니다.");
        }

        response.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/student/lecture")
    public ResponseEntity<?> getLecture(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        ResponseDTO<LiveStationUrlDTO> response = new ResponseDTO<>();

        int userId = customUserDetails.getUser().getId();
        User user = userService.findById(userId);

        if (user.getCourse() != null) {
            int couNo = user.getCourse().getCouNo();
            Lecture lecture = lectureService.getCouLecture(couNo);
            String channelID = lecture.getLiveStationId();

            LiveStationInfoDTO dto = liveStationService.getChannelInfo(channelID);

            if (!dto.getChannelStatus().equals("PUBLISHING")) {
                response.setErrorMessage("진행 중인 강의가 없습니다.");
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                return ResponseEntity.badRequest().body(response);
            }

            List<LiveStationUrlDTO> urlList = liveStationService.getServiceURL(channelID, "GENERAL");
            LiveStationUrlDTO lectureId = LiveStationUrlDTO.builder()
                    .lectureId(lecture.getId())
                    .build();

            response.setItem(lectureId);
            response.setItems(urlList);
            response.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(response);

        } else {
            throw new NoSuchElementException();
        }
    }


    //방송 중인 강의 썸네일 포함
    @GetMapping("/lecture-list")
    public ResponseEntity<?> getAllLectures(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDTO<LectureDTO> response = new ResponseDTO<>();

        //권한 확인
//        int userNo = customUserDetails.getUser().getId();
//        User user = userService.findById(userNo);
//        validateService.validateTeacherAndAdmin(user);

        List<LectureDTO> lectureDTOList = lectureService.getAllLecture();

        if (lectureDTOList.isEmpty()) {
            throw new NoSuchElementException();
        }

        lectureDTOList.forEach(lectureDTO -> {
            String liveStationId = lectureDTO.getLiveStationId();
            List<LiveStationUrlDTO> thumbList = liveStationService.getServiceURL(liveStationId, "THUMBNAIL");
            if (!thumbList.isEmpty()) {
                String thumbnailUrl = thumbList.get(0).getUrl();
                lectureDTO.setLiveThumb(thumbnailUrl);
            }
            lectureDTO.setTeacher(courseService.getCourse(lectureDTO.getCouNo()).getUser().getUserName());
            lectureDTO.setUserCount(lecUserService.userCount(lectureDTO.getId()));
        });

        response.setItems(lectureDTOList);
        response.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(response);
    }


    //방송 중인 강의 썸네일 포함
    @GetMapping("/page/lecture-list")
    public ResponseEntity<?> getLectureList(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "searchCondition", required = false, defaultValue = "all") String category,
                                            @RequestParam(value = "searchKeyword", required = false, defaultValue = "") String keyword,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDTO<LectureDTO> response = new ResponseDTO<>();

        //권한 확인
        int userNo = customUserDetails.getUser().getId();
        User user = userService.findById(userNo);
        validateService.validateTeacherAndAdmin(user);


        lectureService.getLecturePage(page, category, keyword);

        List<LectureDTO> lectureDTOList = lectureService.getAllLecture();

        if (lectureDTOList.isEmpty()) {
            throw new NoSuchElementException();
        }

        lectureDTOList.forEach(lectureDTO -> {
            String liveStationId = lectureDTO.getLiveStationId();
            List<LiveStationUrlDTO> thumbList = liveStationService.getServiceURL(liveStationId, "THUMBNAIL");
            if (!thumbList.isEmpty()) {
                String thumbnailUrl = thumbList.get(0).getUrl();
                lectureDTO.setLiveThumb(thumbnailUrl);
            }
        });

        response.setItems(lectureDTOList);
        response.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(response);
    }
}
