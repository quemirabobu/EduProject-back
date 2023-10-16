package com.bit.eduventure.postIt.entity;

import com.bit.eduventure.User.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_POSTIT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostItEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POSTIT_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "SENDER_ID")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "RECEIVER_ID")
    private User receiver;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "SENT_DATE")
    private LocalDateTime sentDate;

    @Column(name = "READ_DATE")
    private LocalDateTime readDate;

    @Column(name = "REPLIED")
    private boolean replied; // 답장 여부

}
