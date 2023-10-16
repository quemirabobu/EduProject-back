package com.bit.eduventure.notice.Service;


import com.bit.eduventure.notice.Entity.Notice;
import com.bit.eduventure.notice.Repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public Notice create(Notice notice) {
        if (!StringUtils.hasText(notice.getNoticeTitle())
                || !StringUtils.hasText(notice.getNoticeContent())) {
            throw new NullPointerException();
        }
        return noticeRepository.save(notice);
    }

    @Override
    public List<Notice> getNoticeList() {
        return noticeRepository.findAll();
    }

    @Override
    public void deleteNotice(int id) {
        noticeRepository.deleteById(id);
    }

    @Override
    public Notice getNotice (Integer noticeNo) {
        return noticeRepository.findById(noticeNo)
                .orElseThrow(() -> new NoSuchElementException());
    }

    @Override
    @Transactional
    public Notice update(Notice notice) {
        if (!StringUtils.hasText(notice.getNoticeTitle())
                || !StringUtils.hasText(notice.getNoticeContent())) {
            throw new NullPointerException();
        }
        return noticeRepository.save(notice);
    }

    @Override
    public List<Notice> getCourseNoticeList(String claName) {
        return noticeRepository.findAllByClaAndAdmin(claName);
    }

}
