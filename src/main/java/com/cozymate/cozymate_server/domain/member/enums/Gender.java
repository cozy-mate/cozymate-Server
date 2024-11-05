package com.cozymate.cozymate_server.domain.member.enums;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Arrays;

public enum Gender {

    MALE,
    FEMALE
    ;
    @Override
    public String toString(){
        return name();
    }

    public static Gender getValue(String genderString) {
        return Arrays.stream(values())
                .filter(gender -> gender.name().equalsIgnoreCase(genderString))
                .findFirst()
                .orElseThrow(()->new GeneralException(ErrorStatus._INVALID_GENDER));
    }

}
