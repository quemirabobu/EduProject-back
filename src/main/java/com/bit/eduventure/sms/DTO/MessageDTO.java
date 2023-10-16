package com.bit.eduventure.sms.DTO;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class MessageDTO {

    String to;
    String content;
}
