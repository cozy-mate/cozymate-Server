package com.cozymate.cozymate_server.domain.member.enums;

import com.cozymate.cozymate_server.global.utils.EnumValue;

public enum SocialType implements EnumValue {
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

    public static SocialType getValue(String socialTypeString){
        return EnumValue.getValue(SocialType.class,socialTypeString);
    }
}
