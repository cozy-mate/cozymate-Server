package com.cozymate.cozymate_server.global.response.code;

public interface BaseErrorCode {
    public ErrorReasonDto getReasonHttpStatus();
    String getMessage();
}
