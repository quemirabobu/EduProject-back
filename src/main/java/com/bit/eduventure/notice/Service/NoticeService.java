package com.bit.eduventure.notice.Service;


import com.bit.eduventure.notice.Entity.Notice;

import java.util.List;

public interface NoticeService {

    Notice create(Notice notice);

    List<Notice> getNoticeList();

    void deleteNotice(int id);

    Notice getNotice(Integer noticeNo);

    Notice update(Notice notice);

    List<Notice> getCourseNoticeList(String claName);
}
