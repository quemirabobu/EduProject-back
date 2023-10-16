package com.bit.eduventure.notice.Entity;


import com.bit.eduventure.User.Entity.User;
import com.bit.eduventure.notice.DTO.NoticeDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="T_NOTICE")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noticeNo;
    private String claName;
    private String noticeTitle;
    private String date;
    @Column(length = 1500)
    private String noticeContent;
    @Builder.Default
    private LocalDateTime noticeRegdate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "USER_NO")
    private User user;


    public NoticeDTO EntityToDTO(){
        NoticeDTO noticeDTO = NoticeDTO.builder()
                .noticeNo(this.noticeNo)
                .claName(this.claName)
                .noticeTitle(this.noticeTitle)
                .date(this.date)
                .noticeContent(this.noticeContent)
                .noticeRegdate(this.noticeRegdate.toString())
                .userDTO(this.user.EntityToDTO())
                .build();
        return noticeDTO;

    }

}
