package com.bit.eduventure.lecture.repository;

import com.bit.eduventure.lecture.entity.LecUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LecUserRepository extends JpaRepository<LecUser, Integer> {
    List<LecUser> findAllByLecturePK(int lecturePK);
    void deleteAllByLecturePKAndUserPK(int lecturePK, int userPK);
    void deleteAllByLecturePK(int lecturePK);
    boolean existsByLecturePKAndUserPK(int lecturePK, int userPK);

    int countAllByLecturePK(int lecturePK);
}
