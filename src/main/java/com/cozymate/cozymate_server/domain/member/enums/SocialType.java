package com.cozymate.cozymate_server.domain.member.enums;

import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Arrays;

public enum SocialType {
    KAKAO,
    NAVER,
    GOOGLE,
    APPLE,
    TEST;

    // ex. return KAKAO;
    @Override
    public String toString() {
        return name();
    }

    public static SocialType getValue(String socialTypeString) {
        return Arrays.stream(values())
                .filter(socialType -> socialType.name().equalsIgnoreCase(socialTypeString))
                .findFirst()
                .orElseThrow( () -> new GeneralException(ErrorStatus._INVALID_SOCIAL_TYPE));
    }
}
