package com.bit.eduventure.lecture.controller;

import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.jwt.JwtTokenProvider;
import com.bit.eduventure.lecture.dto.ChatMessage;
import com.bit.eduventure.lecture.entity.LecUser;
import com.bit.eduventure.lecture.service.LecUserService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RequestMapping("/liveChat")
@Controller
//@RestController
public class LectureChatController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final LecUserService lecUserService;


    @GetMapping("/abc")
    public String chat() {
        return "/chat/chat";
    }

    @MessageMapping("/sendMsg/{lectureId}")
    @SendTo("/topic/lecture/{lectureId}")
    public String sendMessage(@Header("Authorization") String token,
                              @Payload String chatMessage,
                              @DestinationVariable String lectureId) {
        Gson gson = new Gson();
        try {
            int lecturePK = Integer.parseInt(lectureId);
            ChatMessage jsonMessage = gson.fromJson(chatMessage, ChatMessage.class);

            token = token.substring(7);
            String userId = jwtTokenProvider.validateAndGetUsername(token);

            String userName = userService.findByUserId(userId).getUserName();

            ChatMessage returnMsg = ChatMessage.builder()
                    .content(jsonMessage.getContent())
                    .sender(userName)
                    .time(jsonMessage.getTime())
                    .build();
            List<LecUser> lecUserList = lecUserService.lecUserList(lecturePK);

            if (!lecUserList.isEmpty()) {
                List<String> userList = lecUserList.stream()
                        .map(LecUser::getUserName)
                        .collect(Collectors.toList());
                returnMsg.setUserList(userList);
            }

            return gson.toJson(returnMsg);

        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    @MessageMapping("/sendMsg/{lectureId}/addUser")
    @SendTo("/topic/lecture/{lectureId}") //보내는 곳은 똑같이
    public String addUser(@Header("Authorization") String token,
                          @DestinationVariable String lectureId) {
        Gson gson = new Gson();
        try {
            int lecturePK = Integer.parseInt(lectureId);

            token = token.substring(7);
            String userId = jwtTokenProvider.validateAndGetUsername(token);
            User user = userService.findByUserId(userId);
            int userPK = user.getId();
            String userName = user.getUserName();

            //DB에 강의에 들어온 유저 저장
            lecUserService.enterLecUser(lecturePK, userPK, userName);

            ChatMessage returnMsg = ChatMessage.builder()
                    .content(userName + "님이 입장하였습니다.")
                    .build();

            List<LecUser> lecUserList = lecUserService.lecUserList(lecturePK);

            if (!lecUserList.isEmpty()) {
                List<String> userList = lecUserList.stream()
                        .map(LecUser::getUserName)
                        .collect(Collectors.toList());
                returnMsg.setUserList(userList);
            }

            return gson.toJson(returnMsg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @MessageMapping("/sendMsg/{lectureId}/leave")
    @SendTo("/topic/lecture/{lectureId}") //보내는 곳은 똑같이
    public String leaveUser(@Header("Authorization") String token,
                            @DestinationVariable String lectureId) {
        Gson gson = new Gson();
        try {
            int lecturePK = Integer.parseInt(lectureId);

            token = token.substring(7);
            String userId = jwtTokenProvider.validateAndGetUsername(token);
            User user = userService.findByUserId(userId);
            int userPK = user.getId();
            String userName = user.getUserName();

            ChatMessage returnMsg = ChatMessage.builder()
                    .content(userName + "님이 나가셨습니다.")
                    .build();

            //DB에 강의에 나간 유저 삭제
            lecUserService.leaveLecUser(lecturePK, userPK);

            List<LecUser> lecUserList = lecUserService.lecUserList(lecturePK);

            if (!lecUserList.isEmpty()) {
                List<String> userList = lecUserList.stream()
                        .map(LecUser::getUserName)
                        .collect(Collectors.toList());
                returnMsg.setUserList(userList);
            }

            return gson.toJson(returnMsg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @MessageMapping("/sendMsg/{lectureId}/exit")
    @SendTo("/topic/lecture/{lectureId}") //보내는 곳은 똑같이
    public String exitLecture(@Header("Authorization") String token,
                            @DestinationVariable String lectureId) {
        Gson gson = new Gson();
        try {
            int lecturePK = Integer.parseInt(lectureId);

            token = token.substring(7);
            String userId = jwtTokenProvider.validateAndGetUsername(token);
            User user = userService.findByUserId(userId);
            String userName = user.getUserName();

            ChatMessage returnMsg = ChatMessage.builder()
                    .content(userName + "님이 강의를 종료하셨습니다.")
                    .sender("Server")
                    .exit(true)
                    .build();

            //실시간 강의 유저 리스트 삭제
            lecUserService.deleteLecture(lecturePK);

            return gson.toJson(returnMsg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


}
