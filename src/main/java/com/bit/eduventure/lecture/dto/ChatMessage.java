package com.bit.eduventure.lecture.dto;


import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatMessage {
    @SerializedName("content")
    private String content;
    @SerializedName("sender")
    private String sender;
    @SerializedName("time")
    private String time;
    @SerializedName("exit")
    private boolean exit;
    @SerializedName("userList")
    private List<String> userList;
}