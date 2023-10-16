package com.bit.eduventure.lecture.service;

import com.bit.eduventure.lecture.dto.LectureDTO;
import com.bit.eduventure.lecture.entity.Lecture;
import com.bit.eduventure.lecture.repository.LectureRepository;
import com.bit.eduventure.vodBoard.entity.VodBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LectureService {
    private final LectureRepository lectureRepository;

    public Lecture createLecture(LectureDTO lectureDTO) {
        Lecture lecture = lectureDTO.DTOTOEntity();
        return lectureRepository.save(lecture);
    }

    public Lecture getLecture(int lectureId) {
        return lectureRepository.findById(lectureId)
                .orElseThrow(() -> new NoSuchElementException());
    }

    public Lecture getLectureLiveStationId(String liveStationId) {
        return lectureRepository.findByLiveStationId(liveStationId)
                .orElseThrow(() -> new NoSuchElementException());
    }

    public List<LectureDTO> getAllLecture() {
        List<Lecture> lectureList = lectureRepository.findAll();

        List<LectureDTO> returnList = lectureList.stream()
                .map(Lecture::EntityTODTO)
                .collect(Collectors.toList());

        return returnList;
    }

    //학생별 강의 주소 조회
    public Lecture getCouLecture(int couNo) {
        return lectureRepository.findByCouNo(couNo)
                .orElseThrow(() -> new RuntimeException("해당하는 반의 강의가 없습니다."));
    }

    public void deleteLecture(int lecId) {
        lectureRepository.deleteById(lecId);
    }

    public Page<Lecture> getLecturePage(int page, String category, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        Specification<Lecture> spec = searchByCategory(category, keyword);
        Page<Lecture> vodBoardPage = lectureRepository.findAll(spec, pageable);
        return vodBoardPage;
    }

    private Specification<Lecture> searchByCategory(String category, String kw) {
        return (b, query, cb) -> {
            query.distinct(true);
            String likeKeyword = "%" + kw + "%";
            switch (category) {
                case "title":
                    return cb.like(b.get("title"), likeKeyword);
                case "couNo":
                    return cb.like(b.get("couNo"), likeKeyword);
                default:
                    return cb.or(
                            cb.like(b.get("title"), likeKeyword),
                            cb.like(b.get("couNo"), likeKeyword)
                    );
            }
        };
    }

}
