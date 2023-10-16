package com.bit.eduventure.nchat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {
    private String id;
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("member_id")
    private String memberId;
    private String name;
    private String profile;
    private String memo;
    private String country;
    private String remoteip;
    private String adid;
    private String device;
    private String network;
    private String version;
    private String model;
    private boolean deleted;
    private boolean online;
    @JsonProperty("customField")
    private String customField;
    @JsonProperty("device_type")
    private String[] deviceType;
    private boolean push;
    @JsonProperty("memberblock_id")
    private String memberBlockId;
    @JsonProperty("logined_at")
    private String loginedAt;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("deleted_at")
    private String deletedAt;
}

