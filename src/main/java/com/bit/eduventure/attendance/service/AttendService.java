
package com.bit.eduventure.attendance.service;

import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Service.UserService;

import com.bit.eduventure.attendance.dto.AttendDTO;
import com.bit.eduventure.attendance.entity.Attend;
import com.bit.eduventure.attendance.repository.AttendRepository;

import com.bit.eduventure.timetable.entity.TimeTable;
import com.bit.eduventure.timetable.repository.TimeTableRepository;
import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendService {

    private final AttendRepository attendRepository;
    private final UserService userService;
    private final TimeTableRepository timeTableRepository;


    public class DayOfWeekMapping {
        private static final Map<DayOfWeek, String> DAY_OF_WEEK_KOREAN_MAP = new HashMap<>();

        static {
            DAY_OF_WEEK_KOREAN_MAP.put(DayOfWeek.MONDAY, "월요일");
            DAY_OF_WEEK_KOREAN_MAP.put(DayOfWeek.TUESDAY, "화요일");
            DAY_OF_WEEK_KOREAN_MAP.put(DayOfWeek.WEDNESDAY, "수요일");
            DAY_OF_WEEK_KOREAN_MAP.put(DayOfWeek.THURSDAY, "목요일");
            DAY_OF_WEEK_KOREAN_MAP.put(DayOfWeek.FRIDAY, "금요일");
            DAY_OF_WEEK_KOREAN_MAP.put(DayOfWeek.SATURDAY, "토요일");
            DAY_OF_WEEK_KOREAN_MAP.put(DayOfWeek.SUNDAY, "일요일");
        }

        public static String toKorean(DayOfWeek dayOfWeek) {
            return DAY_OF_WEEK_KOREAN_MAP.get(dayOfWeek);
        }
    }
    // 교시별 시작 시간 매핑
    private static final Map<String, LocalTime> COURSE_START_TIMES = Map.of(
            "1교시", LocalTime.of(14, 0),
            "2교시", LocalTime.of(15, 0),
            "3교시", LocalTime.of(16, 0),
            "4교시", LocalTime.of(17, 0),
            "5교시", LocalTime.of(18, 0),
            "6교시", LocalTime.of(19, 0),
            "7교시", LocalTime.of(21, 0),
            "8교시", LocalTime.of(22, 0)
    );


    public AttendDTO getIsCourseForUser(int userId) {
        LocalDate today = LocalDate.now();

        User user = userService.findById(userId);

        // 오늘 날짜와 해당 사용자의 Attend 기록을 바로 조회합니다.
        Attend attend = attendRepository.findByUserNoAndAttRegDate(userId, today);

        // 오늘 날짜의 Attend 기록이 없으면 새로 만들어줍니다.
        if (attend == null) {
            attend = Attend.builder()
                    .attRegDate(today)
                    .userNo(userId)
                    .build();
        }

        List<TimeTable> timeTableList = timeTableRepository.findAllByCouNo(user.getCourse().getCouNo());

        String currentDayOfWeek = DayOfWeekMapping.toKorean(today.getDayOfWeek());

        Optional<TimeTable> firstTimeTableOfDay = timeTableList.stream()
                .filter(timeTable -> timeTable.getTimeWeek().equals(currentDayOfWeek))
                .sorted(Comparator.comparing(TimeTable::getTimeClass))
                .findFirst();

        if (!firstTimeTableOfDay.isPresent()) {
            System.out.println("오늘은 수업날이 아니다.");
            attend.setIsCourse(false);
        } else {
            attend.setIsCourse(true);
        }

        return attendRepository.save(attend).EntityToDTO();
    }



    public AttendDTO registerAttendance(int userId, LocalDateTime attendTime) {
        // 1. Validate the attendance
        validateAttendance(userId, attendTime);

        LocalDate today = LocalDate.now();
        Attend attend = attendRepository.findByUserNoAndAttRegDate(userId, today);

        // 2. Create and save the attendance record
        attend.setAttStart(createAttendanceRecord(userId, attendTime).getAttStart());
        attend.setAttContent(createAttendanceRecord(userId, attendTime).getAttContent());
        AttendDTO attendDTO = attendRepository.save(attend).EntityToDTO();

        return attendDTO;
    }


    private void validateAttendance(int userId, LocalDateTime attendTime) {
        User user = userService.findById(userId);

        if (user.getCourse() == null) {
            throw new IllegalArgumentException("User is not registered for any course.");
        }

        LocalDate today = attendTime.toLocalDate(); // use the given attendTime's date for consistency
        Attend attend = attendRepository.findByUserNoAndAttRegDate(userId, today);

//        if (attend != null) {
//            throw new IllegalArgumentException("You've already registered your attendance for today.");
//        }

        List<TimeTable> timeTableList = timeTableRepository.findAllByCouNo(user.getCourse().getCouNo());
        String currentDayOfWeek = DayOfWeekMapping.toKorean(attendTime.getDayOfWeek());
        Optional<TimeTable> firstTimeTableOfDay = timeTableList.stream()
                .filter(timeTable -> timeTable.getTimeWeek().equals(currentDayOfWeek))
                .sorted(Comparator.comparing(TimeTable::getTimeClass))
                .findFirst();

        if (!firstTimeTableOfDay.isPresent()) {
            throw new IllegalArgumentException("오늘은 수업시간이 아닙니다.");
        }

        String timeClass = firstTimeTableOfDay.get().getTimeClass();
        LocalTime courseStart = COURSE_START_TIMES.get(timeClass);
        if (courseStart == null) {
            throw new IllegalArgumentException("Invalid course time provided.");
        }

        LocalTime courseEnd = courseStart.plusMinutes(50);

        if (attendTime.isBefore(LocalDateTime.of(attendTime.toLocalDate(), courseStart).minusMinutes(20))
                || attendTime.isAfter(LocalDateTime.of(attendTime.toLocalDate(), courseEnd))) {
            throw new IllegalArgumentException("입실은 수업시간 20분전부터 수업시간 내에서만 가능합니다.");
        }
    }



    private Attend createAttendanceRecord(int userId, LocalDateTime attendTime) {
        Attend record = new Attend();
        record.setUserNo(userId);
        record.setAttStart(attendTime);

        User user = userService.findById(userId);
        List<TimeTable> timeTableList = timeTableRepository.findAllByCouNo(user.getCourse().getCouNo());
        String currentDayOfWeek = DayOfWeekMapping.toKorean(attendTime.getDayOfWeek());
        Optional<TimeTable> firstTimeTableOfDay = timeTableList.stream()
                .filter(timeTable -> timeTable.getTimeWeek().equals(currentDayOfWeek))
                .sorted(Comparator.comparing(TimeTable::getTimeClass))
                .findFirst();

        boolean hasMatchingDay = false;

        for(TimeTable timeTable : timeTableList) {

            boolean isCourse = timeTable.getTimeWeek().equals(currentDayOfWeek);

            if(isCourse) {
                hasMatchingDay = true;  // true가 발견되면 변수를 true로 설정합니다.
            }
        }

        record.setIsCourse(hasMatchingDay);

        String timeClass = firstTimeTableOfDay.get().getTimeClass();
        LocalTime courseStart = COURSE_START_TIMES.get(timeClass);

        LocalDateTime classTime = LocalDateTime.of(attendTime.toLocalDate(), courseStart);

        if (attendTime.isBefore(classTime.plusMinutes(5))) {
            record.setAttContent("출석중");
        } else if (attendTime.isBefore(classTime.plusMinutes(10))) {
            record.setAttContent("1");
        } else {
            record.setAttContent("2");
        }


        return record;
    }


    public void validateExitTime(User user, LocalDateTime exitTime, LocalTime courseEndTime) {
        if (courseEndTime == null) {
            throw new IllegalArgumentException("Invalid course time or day for the user.");
        }
    }


    public Attend processExitTime(int userId, LocalDateTime exitTime) {
        User user = userService.findById(userId);
        int couNo = user.getCourse().getCouNo();

        List<TimeTable> timeTableList = timeTableRepository.findAllByCouNo(couNo);
        String currentDayOfWeek = DayOfWeekMapping.toKorean(exitTime.getDayOfWeek());

        List<String> returnTimeWeekList = timeTableList.stream()
                .map(TimeTable::getTimeWeek)
                .collect(Collectors.toList());

        LocalTime courseEndTime = null;
        if (returnTimeWeekList.contains(currentDayOfWeek)) {
            String timeClass = timeTableList.get(timeTableList.size() - 1).getTimeClass();
            LocalTime courseStart = COURSE_START_TIMES.get(timeClass);
            courseEndTime = courseStart.plusMinutes(50);
        }

        // 검증 로직
        validateExitTime(user, exitTime, courseEndTime);

        LocalDateTime startOfDay = exitTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        List<Attend> records = attendRepository.findByUserNoAndAttStartBetween(user.getId(), startOfDay, endOfDay);

        if (records.isEmpty()) {
            throw new IllegalArgumentException("이 날짜에 해당하는 입실 기록이 없습니다.");
        }

        Attend record = records.get(0);
        record.setAttFinish(exitTime);
        record.setAttDate(exitTime.toLocalDate());

        String currentStatus = record.getAttContent();

        if (currentStatus.equals("출석중") &&
                exitTime.isBefore(LocalDateTime.of(exitTime.toLocalDate(), courseEndTime))) {
            record.setAttContent("0");

        }

        return record;
    }

    public AttendDTO saveAttendanceRecord(Attend attend) {
        return attendRepository.save(attend).EntityToDTO();
    }

    public AttendDTO registerExitTime(int userId, LocalDateTime exitTime) {
        Attend processedAttend = processExitTime(userId, exitTime);
        return saveAttendanceRecord(processedAttend);
    }

    //특정 사용자의 출석 기록 조회
    public List<AttendDTO> getAttendanceRecordsByUser(User user) {
        List<Attend> records = attendRepository.findAllByUserNo(user.getId());
        return records.stream().map(AttendDTO::new).collect(Collectors.toList());
    }

    //특정 사용자 및 특정 날짜의 출석 기록 조회
    public List<AttendDTO> getAttendanceRecordsByUserAndDate(User user, LocalDate date) {
        List<Attend> attendances = attendRepository.findAllByUserNoAndAttDate(user.getId(), date);
        return attendances.stream()
                .map(Attend::EntityToDTO)
                .collect(Collectors.toList());
    }

    //특정 사용자 및 특정 달의 출석 기록 조회
    public List<AttendDTO> getAttendanceRecordsByUserAndMonth(User user, YearMonth yearMonth) {
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Attend> attendances = attendRepository.findByUserNoAndAttDateBetween(user.getId(), startDate, endDate);
        return attendances.stream()
                .map(Attend::EntityToDTO)
                .collect(Collectors.toList());
    }

    public Attend updateAttendRecord(Attend newAttendData) {
        return attendRepository.findById(newAttendData.getId())
                .map(existingAttend -> {
                    Optional.ofNullable(newAttendData.getUserNo()).ifPresent(existingAttend::setUserNo);
                    Optional.ofNullable(newAttendData.getAttStart()).ifPresent(existingAttend::setAttStart);
                    Optional.ofNullable(newAttendData.getAttFinish()).ifPresent(existingAttend::setAttFinish);
                    Optional.ofNullable(newAttendData.getAttDate()).ifPresent(existingAttend::setAttDate);
                    Optional.ofNullable(newAttendData.getAttContent()).ifPresent(existingAttend::setAttContent);
                    return attendRepository.save(existingAttend);
                })
                .orElseThrow(() -> new IllegalArgumentException("Attendance record with the given ID does not exist"));
    }

    public void deleteAttendList(String attIdList) {
        for (int id : jsonList(attIdList)) {
            attendRepository.deleteById(id);
        }
    }

    //요청시 attList 형식  보내기.
    public List<Integer> jsonList(String jsonString) {
        // JSON 문자열을 JsonObject로 변환
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();


        // "attList" 키의 값을 JsonArray로 가져옴
        JsonArray jsonArray = jsonObject.getAsJsonArray("attList");

        // JsonArray를 List<Integer>로 변환
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.get(i).getAsInt());
        }

        return list;
    }




//    public boolean checkIfClassExistsForToday(User user) {
//        String currentDayOfWeek = DayOfWeekMapping.toKorean(LocalDateTime.now().getDayOfWeek());
//
//        // 1. User와 연결된 Course를 찾는다.
//        Course course = user.getCourse();
//
//        // 사용자에게 할당된 수업 목록을 가져옵니다.
//        List<String> timeTableList = courseService.getTimeWeeksByCouNo(course.getCouNo());
//
//        System.out.println("currentDayOfWeek: " + currentDayOfWeek);
//
//        // 사용자의 수업 중 현재 요일과 일치하는 수업이 있는지 확인
//        if (timeTableList.contains(currentDayOfWeek)) {
//            return true; // 일치하는 수업이 있으면 true 반환
//        }
//
//        return false; // 일치하는 수업이 없으면 false 반환
//    }






    // 특정 날짜의 특정 사용자의 기록 수정 한번에 수정.
//    public void updateAttendanceRecord(String jsonString) {
//
//        List<AttendDTO> attendDTOList = attJsonList(jsonString);
//
//        for (AttendDTO attendDTO : attendDTOList) {
////            updatedRecordList.add(attendRepository.findById(id).orElseThrow());
//            attendRepository.save(attendDTO.DTOToEntity());
//        }
////        System.out.println(updatedRecordList);
//
//        // 하루에 한번 출석 기록이 있다고 생각하고 작성.
////        Attend attendToUpdate = attend.get(0); //가장 최신 기록만 취급한다.
////        attendToUpdate.setAttStart(updatedRecord.getAttStart());
////        attendToUpdate.setAttFinish(updatedRecord.getAttFinish());
////        attendToUpdate.setAttContent(updatedRecord.getAttContent());
////
////        attendRepository.save(attendToUpdate);
//    }



    //하나씩 수정
//    public Attend updateAttendRecord(Attend attend) {
//
//        return attendRepository.save(attend);
//    }

    //    public AttendDTO registerAttendance(int userId, LocalDateTime attendTime) {
//        Attend record = new Attend();
//        record.setUserNo(userId);
//        record.setAttStart(attendTime);
//
//        User user = userService.findById(userId);
//
//        if (user.getCourse() == null) {
//            System.out.println("User is not registered for any course.");
//
//            throw new IllegalArgumentException("User is not registered for any course.");
//        }
//
//        // 오늘 날짜의 해당 사용자에 대한 출석 기록 확인
//        LocalDate today = LocalDate.now();
//        List<Attend> existAttendance = attendRepository.findByUserNoAndAttDate(userId, today);
//
//        if(!existAttendance.isEmpty()) {
//            System.out.println("You've already registered your attendance for today.");
//
//            throw new IllegalArgumentException("You've already registered your attendance for today.");
//        }
//
//        List<TimeTable> timeTableList = timeTableRepository.findAllByCouNo(user.getCourse().getCouNo());
//
//        String currentDayOfWeek = DayOfWeekMapping.toKorean(attendTime.getDayOfWeek());
//
//        Optional<TimeTable> firstTimeTableOfDay = timeTableList.stream()
//                .filter(timeTable -> timeTable.getTimeWeek().equals(currentDayOfWeek)) // 해당 요일에 해당하는 항목만 필터링
//                .sorted(Comparator.comparing(TimeTable::getTimeClass)) // timeClass를 기준으로 정렬
//                .findFirst(); // 첫 번째 항목 가져오기
//        System.out.println(!firstTimeTableOfDay.isPresent());
//        System.out.println("여기 불린 불린");
//
//        if (!firstTimeTableOfDay.isPresent()) {
//            System.out.println(!firstTimeTableOfDay.isPresent());
//            System.out.println("  오늘은 수업시간이 아님  ");
//            throw new IllegalArgumentException("오늘은 수업시간이 아닙니다.");
//        }
//
//        String timeClass = firstTimeTableOfDay.get().getTimeClass();
//        LocalTime courseStart = COURSE_START_TIMES.get(timeClass);
//        System.out.println(timeClass);
//        if (courseStart == null) {
//            System.out.println("    invalid course time provided ");
//
//            throw new IllegalArgumentException("Invalid course time provided.");
//        }
//
//        LocalTime courseEnd = courseStart.plusMinutes(50);
//
//        if(attendTime.isBefore(LocalDateTime.of(attendTime.toLocalDate(), courseStart))
//                || attendTime.isAfter(LocalDateTime.of(attendTime.toLocalDate(), courseEnd))) {
//            System.out.println("  입실은 수업시간 내에서만 가능  ");
//
//            throw new IllegalArgumentException("입실은 수업시간 내에서만 가능합니다.");
//        }
//
//        if (attendTime.isBefore(LocalDateTime.of(attendTime.toLocalDate(), courseStart))) {
//            record.setAttContent("출석중");
//        } else if (attendTime.isBefore(LocalDateTime.of(attendTime.toLocalDate(), courseStart).plusMinutes(10))) {
//            record.setAttContent("1");
//        } else {
//            record.setAttContent("2");
//        }
//        System.out.println(record);
//
//        System.out.println("서비스의 레코드이다.");
//
//        AttendDTO attendDTO = attendRepository.save(record).EntityToDTO();
//        System.out.println(attendDTO);
//        System.out.println("세이브 후에 들어온건가 어텐드디티오");
//        return attendDTO;
//    }




    //    public AttendDTO registerExitTime(int userId, LocalDateTime exitTime) {
//        User user = userService.findById(userId);
//        // 1. User와 연결된 Course를 찾는다.
//        int couNo = user.getCourse().getCouNo();
//
//        Course course = courseService.getCourse(couNo);
//
////        if (course == null) {
////            throw new IllegalArgumentException("User is not registered for any course.");
////        }
//
//        List<TimeTable> timeTableList = timeTableRepository.findAllByCouNo(couNo);
//
//        String currentDayOfWeek = DayOfWeekMapping.toKorean(exitTime.getDayOfWeek());
//        LocalDate currentDate = exitTime.toLocalDate();
//
//
//        List<String> returnTimeWeekList = new ArrayList<>();
//
//        System.out.println("currentDayOfWeek: " + currentDayOfWeek);
//
//        for(TimeTable test : timeTableList) {
//            returnTimeWeekList.add(test.getTimeWeek());
//        }
//
//        LocalTime courseEndTime = null;
//        System.out.println("timeTableList.toString(): " + timeTableList.toString());
//        if (returnTimeWeekList.contains(currentDayOfWeek)) {
//            String timeClass = timeTableList.get(timeTableList.size() - 1).getTimeClass(); //첫번째 시간을 뽑아내겠다.
//            LocalTime courseStart = COURSE_START_TIMES.get(timeClass);
//            courseEndTime = courseStart.plusMinutes(50); // 강의는 50분 간격이라고 했으므로
//            System.out.println(courseEndTime);
//        }
////        System.out.println("timeClass: " + timeClass);
//        System.out.println(currentDayOfWeek);
//        System.out.println(courseEndTime);
//
//        // 퇴실 시간 제한 조건
////        if(exitTime.isBefore(LocalDateTime.of(exitTime.toLocalDate(), courseEndTime.minusMinutes(10)))
////                || exitTime.isAfter(LocalDateTime.of(exitTime.toLocalDate(), LocalTime.of(23, 59, 59)))) {
////            throw new IllegalArgumentException("퇴실은 수업 종료 10분 전부터 당일까지만 가능합니다.");
////        }
//
//
//        if (courseEndTime == null) {
//            throw new IllegalArgumentException("Invalid course time or day for the user.");
//        }
//
//        LocalDateTime startOfDay = exitTime.toLocalDate().atStartOfDay();
//        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
//
//        List<Attend> records = attendRepository.findByUserNoAndAttStartBetween(user.getId(), startOfDay, endOfDay);
//
//        //첫번째 기록 가져오기
//        Attend record = records.get(0);
//        System.out.println(record);
//
//        if (records.isEmpty()) {
//            System.out.println(" 이날짜에 해당하는 입실 기록이 없음   ");
//
//            throw new IllegalArgumentException("이 날짜에 해당하는 입실 기록이 없습니다.");
//        }
//
//        record.setAttFinish(exitTime);
//        record.setAttDate(currentDate);
//
//        // 현재의 출석 상태를 확인
//        String currentStatus = record.getAttContent();
//
//        // attContent가 "출석중"이 아니면, 상태를 변경하지 않는다.
//        if (!"출석중".equals(currentStatus)) {
//            AttendDTO attendDTO = attendRepository.save(record).EntityToDTO();
//            return attendDTO;
//        }
//
//        // 출석 상태 결정
//        if (exitTime.isBefore(LocalDateTime.of(exitTime.toLocalDate(), courseEndTime))) {
//            record.setAttContent("0");
//        } else {
//            record.setAttContent("2");
//        }
//
//        AttendDTO attendDTO = attendRepository.save(record).EntityToDTO();
//        return attendDTO;
//
//
//    }




}
