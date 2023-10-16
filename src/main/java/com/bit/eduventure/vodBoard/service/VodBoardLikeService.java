package com.bit.eduventure.vodBoard.service;

import com.bit.eduventure.vodBoard.dto.VodBoardLikeDTO;
import com.bit.eduventure.vodBoard.entity.VodBoardLike;
import com.bit.eduventure.vodBoard.repository.VodBoardLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VodBoardLikeService {

    private final VodBoardLikeRepository vodBoardLikeRepository;

    //유저가 좋아요 누르기
    @Transactional
    public void likeVodBoard(int vodNo, int userNo) {
        if (vodNo == 0 || userNo == 0) {
            throw new NullPointerException();
        }
        VodBoardLike vodBoardLike = VodBoardLike.builder()
                .vodNo(vodNo)
                .userNo(userNo)
                .build();
        vodBoardLikeRepository.save(vodBoardLike);
    }

    //유저가 게시물 눌렀는 지 확인
    public int getLikeStatue(int vodNo, int userNo) {
        if (vodBoardLikeRepository.existsByVodNoAndUserNo(vodNo, userNo)) {
            return 1;
        } else {
            return 0;
        }
    }

    //게시물 좋아요 개수 구하기
    @Transactional
    public int getLikeCount(int vodNo) {
        return vodBoardLikeRepository.countAllByVodNo(vodNo);
    }

    //개별 좋아요 삭제
    @Transactional
    public void unlikeVodBoard(int vodNo, int userNo) {
        vodBoardLikeRepository.deleteAllByVodNoAndUserNo(vodNo, userNo);
    }

    //게시물 삭제시 좋아요 전체 삭제
    @Transactional
    public void deleteVodBoard(int vodNo) {
        vodBoardLikeRepository.deleteAllByVodNo(vodNo);
    }
}
