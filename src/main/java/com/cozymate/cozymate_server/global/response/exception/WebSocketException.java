package com.cozymate.cozymate_server.global.response.exception;

import com.cozymate.cozymate_server.global.response.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class WebSocketException extends RuntimeException {

    private final BaseErrorCode code;

    public WebSocketException(BaseErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
