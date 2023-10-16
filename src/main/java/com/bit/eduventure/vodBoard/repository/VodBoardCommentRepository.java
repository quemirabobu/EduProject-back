package com.bit.eduventure.vodBoard.repository;


import com.bit.eduventure.vodBoard.entity.VodBoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VodBoardCommentRepository extends JpaRepository<VodBoardComment, Integer> {
    //게시물 번호에 해당하는 모든 댓글 불러오기
    List<VodBoardComment> findAllByVodNo(int vodNo);
    void deleteAllByVodNo(int vodNo);
    void deleteAllByVodCmtParentNo(int parentNo);
}
