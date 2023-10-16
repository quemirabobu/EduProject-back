package com.bit.eduventure.exception.response;

import com.google.gson.annotations.SerializedName;
import com.bit.eduventure.exception.errorCode.ErrorCode;
import lombok.Getter;
import lombok.ToString;

@ToString

@Getter
public class ErrorResponse {
    @SerializedName("statusCode")
    private int code;

    @SerializedName("errorMessage")
    private String message;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
