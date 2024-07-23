package com.cozymate.cozymate_server.global.response.handler;

import com.cozymate.cozymate_server.global.response.code.BaseErrorCode;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

public class MemberStatHandler extends GeneralException {
    public MemberStatHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
