package com.bit.eduventure.vodBoard.repository;

import com.bit.eduventure.vodBoard.entity.VodBoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VodBoardLikeRepository extends JpaRepository<VodBoardLike, Integer> {
    boolean existsByVodNoAndUserNo(int vodNo, int userNo);
    int countAllByVodNo(int vodNo);
    void deleteAllByVodNo(int vodNo);

    void deleteAllByVodNoAndUserNo(int vodNo, int userNo);
}
