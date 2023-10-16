package com.bit.eduventure.User.Controller;


import com.bit.eduventure.User.DTO.JoinDTO;
import com.bit.eduventure.dto.ResponseDTO;
import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.User.Entity.CustomUserDetails;
import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Repository.UserRepository;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.course.Entity.Course;
import com.bit.eduventure.course.Service.CourseService;
import com.bit.eduventure.Email.Service.EmailService;
import com.bit.eduventure.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CourseService courseService;

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable int id) {
        ResponseDTO<Map<String, String>> responseDTO =
                new ResponseDTO<Map<String, String>>();
        try {
            userService.deleteUser(id);
            Map<String, String> returnMap = new HashMap<String, String>();
            returnMap.put("msg", "정상적으로 삭제되었습니다.");
            responseDTO.setItem(returnMap);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/deleteselectusers")
    public ResponseEntity<?> deleteselectedusers(@RequestBody Map<String, List<Integer>> requestBody) {
        List<Integer> selectedUserIds = requestBody.get("selectedUserIds");
        System.out.println("여기가 셀렉티드 아이디즈");
        System.out.println(selectedUserIds);

        ResponseDTO<Map<String, String>> responseDTO =
                new ResponseDTO<Map<String, String>>();
        try {
            for (int i = 0; i < selectedUserIds.size(); i++) {
                System.out.println(selectedUserIds.get(i));
                userService.deleteUser(selectedUserIds.get(i));
            }


            Map<String, String> returnMap = new HashMap<String, String>();
            returnMap.put("msg", "정상적으로 삭제되었습니다.");
            responseDTO.setItem(returnMap);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    @GetMapping("/user-list")
    public ResponseEntity<?> getUserList(@PageableDefault(page = 0, size = 1000) Pageable pageable,
                                         @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestParam(value = "searchCondition", required = false) String searchCondition,
                                         @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();

        System.out.println(searchCondition);
        System.out.println(searchKeyword);
        System.out.println("이게 서치콘디션, 키워드");
        try {
            searchCondition = searchCondition == null ? "all" : searchCondition;
            searchKeyword = searchKeyword == null ? "" : searchKeyword;
            System.out.println(searchCondition);
            System.out.println(searchKeyword);
            System.out.println("이게 트라이 안에서  키워드");
            Page<User> pageUser = userService.getUserList(pageable, searchCondition, searchKeyword);
            System.out.println(pageUser);
            System.out.println("이게 페이지유저");
            Page<UserDTO> pageUserDTO = pageUser.map(user ->
                    UserDTO.builder()
                            .id(user.getId()).userScore(user.getUserScore())
                            .userId(user.getUserId()).courseDTO(user.getCourse().EntityToDTO())
                            .userPw(user.getUserPw()).userBus(user.getUserBus())
                            .userType(user.getUserType()).userSpecialNote(user.getUserSpecialNote())
                            .userConsultContent(user.getUserConsultContent()).approval(user.getApproval())
                            .userName(user.getUserName())
                            .userTel(user.getUserTel()).userAddressDetail(user.getUserAddressDetail())
                            .userRegdate(user.getUserRegdate())
                            .role(user.getRole()).userBirth(user.getUserBirth()).userSchool(user.getUserSchool())
                            .userAddress(user.getUserAddress()).userJoinId(user.getUserJoinId())
                            .build()
            );
            System.out.println("이게 페이지유저 디티오");

            responseDTO.setPageItems(pageUserDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            responseDTO.setErrorMessage(e.getMessage());

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
/// 되나 한번 보는
    /////여기에
    @PostMapping("/getuser")
    public ResponseEntity<?> getuser(@RequestBody UserDTO user) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();
        User userme;
        if (user.getId() == null) {
            userme = userService.findByUserId(user.getUserId());
        } else {
            userme = userService.findById(user.getId());
        }
        UserDTO userDTO = userme.EntityToDTO();
        userDTO.setUserPw(userDTO.getUserPw());
        try {

            responseDTO.setItem(userDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    @PostMapping("/getuserbytoken")
    public ResponseEntity<?> getuserbytoken(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        System.out.println(customUserDetails);
        System.out.println("토큰으로  사람 불러오는거 들어옴");
        System.out.println(customUserDetails.getUser().getUserId());
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();
        User userme;

        userme = userService.findByUserId(customUserDetails.getUser().getUserId());

        UserDTO userDTO = userme.EntityToDTO();
        userDTO.setUserPw(userDTO.getUserPw());
        try {

            responseDTO.setItem(userDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO.setErrorMessage(e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


    @PostMapping("/getstudent")
    public ResponseEntity<?> getstudent(@RequestBody UserDTO student) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();

        User userMe;

        if (userRepository.existsById(student.getId())) {
            userMe = userService.findById(student.getId());
        } else {
            userMe = new User();
            userMe.setUserTel("엄마없음");
            userMe.setCourse(new Course().builder().couNo(1).build());
        }

        System.out.println(userMe);
        UserDTO userDTO = userMe.EntityToDTO();

        responseDTO.setItem(userDTO);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);

    }


    @PostMapping("/id-check")
    public ResponseEntity<?> idCheck(@RequestBody UserDTO userDTO) {
        ResponseDTO<Map<String, String>> responseDTO =
                new ResponseDTO<>();

        User user = userService.idCheck(userDTO.getUserId());
        Map<String, String> returnMap = new HashMap<>();
        if (user == null) {
            returnMap.put("idCheckMsg", "idOk");
        } else {
            returnMap.put("idCheckMsg", "idFail");
        }

        responseDTO.setItem(returnMap);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinDTO joinDTO) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();
        int couNo = joinDTO.getUserDTO().getCouNo();
        UserDTO userDTO = joinDTO.getUserDTO();
        UserDTO parentDTO = joinDTO.getParentDTO();
        userDTO.setApproval("o");
        parentDTO.setApproval("o");
        System.out.println(userDTO);
        System.out.println(parentDTO);

        User parent = parentDTO.DTOToEntity();
        User user = userDTO.DTOToEntity();

        user.setUserPw(
                passwordEncoder.encode(userDTO.getUserPw())
        );
        parent.setUserPw(passwordEncoder.encode(parentDTO.getUserPw()));
        user.setRole("ROLE_USER");
        parent.setRole("ROLE_USER");
        //회원가입처리(화면에서 보내준 내용을 디비에 저장)
        if (couNo != 0) {
            Course course = courseService.getCourse(couNo);
            user.setCourse(course);
            parent.setCourse(course);
        }
        User joinUser = userService.join(user);
        parent.setUserJoinId(joinUser.getId());
        User joinParent = userService.join(parent);
        joinUser.setUserJoinId(joinParent.getId());
        joinUser = userService.createUser(joinUser);
        joinUser.setUserPw("");
        joinParent.setUserPw("");
        UserDTO joinUserDTO = joinUser.EntityToDTO();
        UserDTO joinParentDTO = joinParent.EntityToDTO();
        responseDTO.setItem(joinParentDTO);
        responseDTO.setItem(joinUserDTO);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }


    @PostMapping("/adminjoin")
    public ResponseEntity<?> adminjoin(@RequestBody UserDTO memberDTO) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();
        System.out.println("admin가입에 들어왔다.");
        System.out.println(memberDTO);

        User user = memberDTO.DTOToEntity();

        user.setUserPw(
                passwordEncoder.encode(memberDTO.getUserPw())
        );
        user.setApproval("x");
        user.setRole("ROLE_ADMIN");
        User joinUser = userService.join(user);
        joinUser.setUserPw("");
        UserDTO joinUserDTO = joinUser.EntityToDTO();
        responseDTO.setItem(joinUserDTO);
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }


    @PutMapping("/admin/update")
    public ResponseEntity<?> adminUpdate(@RequestBody JoinDTO joinDTO) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();

        UserDTO updateSon = joinDTO.getUserDTO();
        UserDTO updatePar = joinDTO.getParentDTO();

        User sonUser = userService.findByUserId(updateSon.getUserId());
        User parUser = userService.findByUserId(updatePar.getUserId());

        Course course = courseService.getCourse(updateSon.getCouNo());

        sonUser.setCourse(course);
        userService.updateUser(sonUser, updateSon);

        parUser.setCourse(course);
        userService.updateUser(parUser, updatePar);

        responseDTO.setItem("수정 되었습니다.");
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserDTO userDTO) {
        ResponseDTO<String> responseDTO = new ResponseDTO<>();

        User updateUser = userService.findByUserId(userDTO.getUserId());

        userService.updateUser(updateUser, userDTO);

        responseDTO.setItem("수정 되었습니다.");
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/updatepassword")
    public ResponseEntity<?> updatepassword(@RequestBody UserDTO userDTO) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();
        System.out.println(userDTO);

        User userBulk = userService.findById(userDTO.getId());

        userBulk.setUserPw(passwordEncoder.encode(userDTO.getUserPw()));
        System.out.println("이것은 유저버크");
        System.out.println(userBulk);

        User joinUser = userService.createUser(userBulk);
        System.out.println("parent가입도 됨");
        joinUser.setUserPw("");
        UserDTO joinUserDTO = joinUser.EntityToDTO();
        responseDTO.setItem(joinUserDTO);
        responseDTO.setStatusCode(HttpStatus.OK.value());
        return ResponseEntity.ok().body(responseDTO);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO,
                                   HttpSession session) {
        System.out.println(userDTO);
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();

        //메시지를 담을 맵 선언
        Map<String, String> returnMap = new HashMap<>();

        //아이디가 존재하면 해당 아이디에 대한 유저정보가 담김
        //아이디가 존재하지 않으면 null이 담김
        User user = userService.login(userDTO.getUserId(), userDTO.getUserPw());
        System.out.println("유저서비스 로그인 성공");
        if (user != null) {
            String token = jwtTokenProvider.create(user);
            user.setUserPw("");
            System.out.println("유저서비스 로그인 성공후 jwtTokenProvider");

            UserDTO loginUserDTO = user.EntityToDTO();
            loginUserDTO.setToken(token);

            responseDTO.setItem(loginUserDTO);
            responseDTO.setStatusCode(HttpStatus.OK.value());

            return ResponseEntity.ok().body(responseDTO);
        } else {
            throw new RuntimeException("login failed");

        }
    }

    @GetMapping("/type-list/{userType}")
    public ResponseEntity<?> getTeacherList(@PathVariable String userType) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();

        List<User> userList = userService.getUserTypeList(userType);
        List<UserDTO> userDTOList = userList.stream()
                .filter(user -> !(userType.equals("teacher") && user.getApproval().equals("x")))
                .map(user -> {
                    UserDTO userDTO = user.EntityToDTO();
                    if (userType.equals("student")) {
                        Integer id = user.getUserJoinId();
                        if (id != null) {
                            if (id != 0) {
                                UserDTO dto = userService.findById(id).EntityToDTO();
                                userDTO.setParentDTO(dto);
                            }
                        }
                    }
                    return userDTO;
                })
                .collect(Collectors.toList());


        responseDTO.setItems(userDTOList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/admin/teacher-list")
    public ResponseEntity<?> getTeacherList() {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();

        List<User> userList = userService.getUserTypeList("teacher");

        List<UserDTO> userDTOList = userList.stream()
                .map(User::EntityToDTO)
                .collect(Collectors.toList());


        responseDTO.setItems(userDTOList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    //반별 학생 정보 찾기
    @GetMapping("/{couNo}/user-list")
    public ResponseEntity<?> getCourseUserList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                               @PathVariable int couNo) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();

        String userType = "student";
        List<User> userList = userService.getUserTypeList(userType);

        //엔티티 리스트를 dto 리스트로 변환하면서 반 번호에 맞는 유저만 저장
        List<UserDTO> userDTOList = userList.stream()
                .map(User::EntityToDTO)
                .filter(user -> Optional.ofNullable(user.getCourseDTO())
                        .map(courseDTO -> courseDTO.getCouNo() == couNo)
                        .orElse(false))
                .collect(Collectors.toList());


        responseDTO.setItems(userDTOList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getStudentUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable int id) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();

        UserDTO userDTO = userService.findById(id).EntityToDTO();
        if (userDTO.getUserJoinId() != 0) {
            UserDTO parentDTO = userService.findById(userDTO.getUserJoinId()).EntityToDTO();
            userDTO.setParentDTO(parentDTO);
        }
        responseDTO.setItem(userDTO);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/myInfo")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();
        int userNo = customUserDetails.getUser().getId();

        UserDTO userDTO = userService.findById(userNo).EntityToDTO();

        responseDTO.setItem(userDTO);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/page/user-list")
    public ResponseEntity<?> getUserList(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                          @RequestParam(value = "category", required = false, defaultValue = "all") String category,
                                          @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();

        Page<User> pageBoard = userService.getUserPage(page, category, keyword);

        Page<UserDTO> pageList = new PageImpl<>(
                pageBoard.get().map(User::EntityToDTO).collect(Collectors.toList()),
                pageBoard.getPageable(),
                pageBoard.getTotalElements()
        );

        responseDTO.setPageItems(pageList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/page/user-list/{userType}")
    public ResponseEntity<?> getTypeUserList(@PathVariable String userType,
                                             @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                             @RequestParam(value = "category", required = false, defaultValue = "all") String category,
                                             @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        ResponseDTO<UserDTO> responseDTO = new ResponseDTO<>();
        System.out.println(userType);
        Page<User> userList = userService.getUserTypePage(userType, page, category, keyword);

        List<UserDTO> userDTOList = userList.stream()
                .filter(user -> !(userType.equals("teacher") && user.getApproval().equals("x")))
                .map(user -> {
                    UserDTO userDTO = user.EntityToDTO();
                    if (userType.equals("student")) {
                        Integer id = user.getUserJoinId();
                        if (id != null) {
                            if (id != 0) {
                                UserDTO dto = userService.findById(id).EntityToDTO();
                                userDTO.setParentDTO(dto);
                            }
                        }
                    }
                    return userDTO;
                })
                .collect(Collectors.toList());


        responseDTO.setItems(userDTOList);
        responseDTO.setStatusCode(HttpStatus.OK.value());

        return ResponseEntity.ok().body(responseDTO);
    }
}
