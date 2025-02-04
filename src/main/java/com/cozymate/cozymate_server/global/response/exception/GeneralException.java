package com.cozymate.cozymate_server.global.response.exception;

import com.cozymate.cozymate_server.global.response.code.BaseErrorCode;
import com.cozymate.cozymate_server.global.response.code.ErrorReasonDto;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final BaseErrorCode code;

    public GeneralException(String message, BaseErrorCode code) {
        super(message);
        this.code = code;
    }

    public GeneralException(BaseErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public ErrorReasonDto getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
    
}