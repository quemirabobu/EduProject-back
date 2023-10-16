package com.bit.eduventure.lecture.service;

import com.bit.eduventure.lecture.entity.LecUser;
import com.bit.eduventure.lecture.repository.LecUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LecUserService {
    private final LecUserRepository lecUserRepository;

    @Transactional
    public void enterLecUser(int lecturePK, int userPK, String userName) {
        if (!lecUserRepository.existsByLecturePKAndUserPK(lecturePK, userPK)) {
            LecUser lecUser = LecUser.builder()
                    .lecturePK(lecturePK)
                    .userPK(userPK)
                    .userName(userName)
                    .build();
            lecUserRepository.save(lecUser);
            lecUserRepository.flush();
        }
    }

    @Transactional
    public void leaveLecUser(int lecturePK, int userPK) {
        lecUserRepository.deleteAllByLecturePKAndUserPK(lecturePK, userPK);
        lecUserRepository.flush();
    }

    public List<LecUser> lecUserList(int lecturePK) {
        return lecUserRepository.findAllByLecturePK(lecturePK);
    }

    public void deleteLecture(int lecturePK) {
        lecUserRepository.deleteAllByLecturePK(lecturePK);
    }

    public int userCount(int lecturePK) {
        return lecUserRepository.countAllByLecturePK(lecturePK);
    }
}
