package com.bit.eduventure.vodBoard.repository;


import com.bit.eduventure.vodBoard.entity.VodBoardFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VodBoardFileRepository extends JpaRepository<VodBoardFile, Integer> {
    List<VodBoardFile> findAllByVodBoardNo(int boardNo);
    void deleteAllByVodBoardNo(int vodNo);
}
