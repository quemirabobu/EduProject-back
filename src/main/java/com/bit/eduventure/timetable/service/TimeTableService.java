package com.bit.eduventure.timetable.service;

import com.bit.eduventure.timetable.dto.TimeTableDTO;
import com.bit.eduventure.course.Entity.Course;
import com.bit.eduventure.course.Repository.CourseRepository;
import com.bit.eduventure.timetable.entity.TimeTable;
import com.bit.eduventure.timetable.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final CourseRepository courseRepository;
    private final TimeTableRepository timeTableRepository;

    /* 시간표 등록 */
    public void  registerTimetable(TimeTableDTO requestDTO) {

        // TimeTableDTO
        TimeTableDTO tableDTO = TimeTableDTO.builder()
                .couNo(requestDTO.getCouNo())
                .timeNo(requestDTO.getTimeNo())
                .claName(requestDTO.getClaName())
                .timeWeek(requestDTO.getTimeWeek())
                .timeClass(requestDTO.getTimeClass())
                .timePlace(requestDTO.getTimePlace())
                .timeColor(requestDTO.getTimeColor())
                .timeTitle(requestDTO.getTimeTitle())
                .timeTeacher(requestDTO.getTimeTeacher())
                .build();

        // 데이터베이스에 저장
        timeTableRepository.save(tableDTO.DTOTOEntity());
    }

    /* couNo을 기반으로 TimeTable 목록 조회 */
    public List<TimeTableDTO> getTimetablesByCouNo(int couNo) {
        List<TimeTable> timeTableList = timeTableRepository.findAllByCouNo(couNo);

        List<TimeTableDTO> returnList = new ArrayList<>();

        for (TimeTable timeTable : timeTableList) {
            TimeTableDTO dto = timeTable.EntityTODTO();
            returnList.add(dto);
        }
        return returnList;
    }


    /* 시간표 전체 조회 */
    public List<TimeTableDTO> getAllTimetables() {

        List<TimeTable> timeTableList = timeTableRepository.findAll();
        List<TimeTableDTO> returnList = new ArrayList<>();
        System.out.println("시간표 서비스 returnList1==========="+returnList);

        for (TimeTable timeTable : timeTableList) {

            TimeTableDTO dto = TimeTableDTO.builder()
                    .timeNo(timeTable.getTimeNo())
                    .couNo(timeTable.getCouNo())
                    .claName(timeTable.getClaName())
                    .timeWeek(timeTable.getTimeWeek())
                    .timeClass(timeTable.getTimeClass())
                    .timePlace(timeTable.getTimePlace())
                    .timeColor(timeTable.getTimeColor())
                    .timeTitle(timeTable.getTimeTitle())
                    .timeTeacher(timeTable.getTimeTeacher())
                    .build();
            returnList.add(dto);
        }

        System.out.println("시간표 서비스 returnList2=============="+returnList);
        return returnList;
    }

    /* 시간표 삭제 */
    public void deleteTimetable(String claName, String timeWeek) {
        List<TimeTable> timeTables = timeTableRepository.findByClaNameAndTimeWeek(claName, timeWeek);
        if (!timeTables.isEmpty()) {
            for (TimeTable timeTable : timeTables) {
                timeTableRepository.delete(timeTable);
            }
        } else {
            throw new IllegalArgumentException("TimeTable not found");
        }
    }

    public List<TimeTable> getTimeTableListForClaName(String claName) {
        return timeTableRepository.findAllByClaName(claName);
    }

    /* 학생별 시간표 리스트 */
    public List<TimeTableDTO> getTimetableByStudent(int couNo) {

        List<TimeTable> timeTableList = timeTableRepository.findAllByCouNo(couNo);
        List<TimeTableDTO> returnList = new ArrayList<>();
        Map<String, TimeTableDTO> groupedMap = new HashMap<>();

        for (TimeTable timeTable : timeTableList) {
            Course course = courseRepository.findByClaName(timeTable.getClaName())
                    .orElseThrow(() -> new NoSuchElementException());

            if (course == null) {
                System.out.println("No course found for claName: " + timeTable.getClaName());
                continue;
            }

            // 조합을 문자열로 생성
            String combination = timeTable.getTimeWeek() + "-" + timeTable.getTimeTitle();

            if (groupedMap.containsKey(combination)) {
                TimeTableDTO existingDto = groupedMap.get(combination);
                // 기존 DTO의 timeClass에 새로운 timeClass 추가
                existingDto.setTimeClass(existingDto.getTimeClass() + "," + timeTable.getTimeClass());
            } else {
                TimeTableDTO dto = TimeTableDTO.builder()
                        .timeNo(timeTable.getTimeNo())
                        .couNo(timeTable.getTimeNo())
                        .claName(course.getClaName())
                        .timeWeek(timeTable.getTimeWeek())
                        .timeClass(timeTable.getTimeClass())
                        .timePlace(timeTable.getTimePlace())
                        .timeColor(timeTable.getTimeColor())
                        .timeTitle(timeTable.getTimeTitle())
                        .timeTeacher(timeTable.getTimeTeacher())
                        .build();
                groupedMap.put(combination, dto);
            }
        }

        returnList.addAll(groupedMap.values());
        return returnList;
    }

    @Transactional
    public void deleteAllCourse(int couNo) {
        timeTableRepository.deleteAllByCouNo(couNo);
    }

}