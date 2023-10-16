package com.bit.eduventure.User.Service;

import com.bit.eduventure.User.DTO.UserDTO;
import com.bit.eduventure.User.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    User idCheck(String userId);
    User join(User user);
    User createUser(User user);
    User updateUser(User user, UserDTO userDTO);
    User findById(int id);
    User findByUserId(String userId);
    void modify(User modifyUser);
    User login(String userId, String userPw);
    Page<User> getUserList(Pageable pageable, String searchCondition, String searchKeyword);
    void deleteUser(int id);
    //권한에 맞는 유저 리스트 찾기
    List<User> getUserTypeList(String userType);
    void increaseuserscore(Integer id);
    long getUserCountCourse(int couNo);
    List<User> getUserListForCouNo(int couNo);

    Page<User> getUserPage(int page, String category, String keyword);
    Page<User> getUserTypePage(String type, int page, String category, String keyword);
}
