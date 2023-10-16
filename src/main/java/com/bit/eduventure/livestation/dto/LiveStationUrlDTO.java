package com.bit.eduventure.livestation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LiveStationUrlDTO {
    private int lectureId;
    private String channelId;
    private String name;
    private String url;
}
