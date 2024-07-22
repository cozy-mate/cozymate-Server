package com.cozymate.cozymate_server.global.response.handler;

import com.cozymate.cozymate_server.global.response.code.BaseErrorCode;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

public class MemberHandler extends GeneralException {
    public MemberHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}