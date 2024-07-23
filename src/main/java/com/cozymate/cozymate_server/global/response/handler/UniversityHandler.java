package com.cozymate.cozymate_server.global.response.handler;

import com.cozymate.cozymate_server.global.response.code.BaseErrorCode;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

public class UniversityHandler extends GeneralException {
    public UniversityHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
