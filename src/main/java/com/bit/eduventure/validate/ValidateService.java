package com.bit.eduventure.validate;

import com.bit.eduventure.User.Entity.User;
import org.springframework.stereotype.Service;

@Service
public class ValidateService {
    public void validateTeacherAndAdmin(User user) {
        if (!user.getUserType().equals("teacher")
                && !user.getUserType().equals("admin")) {
            throw new RuntimeException("권한이 없습니다.");
        }
    }
}
