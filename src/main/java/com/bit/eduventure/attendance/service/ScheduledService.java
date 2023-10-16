package com.bit.eduventure.attendance.service;


import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.User.Service.UserService;
import com.bit.eduventure.attendance.entity.Attend;
import com.bit.eduventure.attendance.repository.AttendRepository;
import com.bit.eduventure.timetable.entity.TimeTable;
import com.bit.eduventure.timetable.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduledService {
    private final AttendRepository attendRepository;
    private final UserService userService;
    private final TimeTableRepository timeTableRepository;

    @Scheduled(cron = "0 5 0 * * ?")
    public void createStudentAttend() {
        LocalDate today = LocalDate.now();

        List<User> studentList = userService.getUserTypeList("student");

        for (User user : studentList) {
            List<TimeTable> timeTableList = timeTableRepository.findAllByCouNo(user.getCourse().getCouNo());

            String currentDayOfWeek = AttendService.DayOfWeekMapping.toKorean(today.getDayOfWeek());

            Optional<TimeTable> firstTimeTableOfDay = timeTableList.stream()
                    .filter(timeTable -> timeTable.getTimeWeek().equals(currentDayOfWeek))
                    .sorted(Comparator.comparing(TimeTable::getTimeClass))
                    .findFirst();

            if (firstTimeTableOfDay.isPresent()) {
                Attend attend = attendRepository.findByUserNoAndAttRegDate(user.getId(), today);
                if (attend == null) {
                    attend = Attend.builder()
                            .attRegDate(today)
                            .isCourse(true)
                            .build();
                    attendRepository.save(attend);
                }
            }
        }
    }

    //자정마다 퇴실이 없으면 결석(2)으로 변경 코드
    @Scheduled(cron = "0 10 0 * * ?")
    public void updateAttContentForNullAttFinish() {
        LocalDate yesterDay = LocalDate.now().minusDays(1);

        List<Attend> recordsWithNullAttFinish = attendRepository.findByAttFinishIsNullAndAttRegDate(yesterDay);

        for (Attend record : recordsWithNullAttFinish) {
            record.setAttContent("2");
            attendRepository.save(record);
        }
    }

}
