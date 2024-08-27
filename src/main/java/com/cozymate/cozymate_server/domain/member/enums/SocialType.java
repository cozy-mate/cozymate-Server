package com.cozymate.cozymate_server.domain.member.enums;

import java.util.Arrays;
import java.util.Optional;

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

    public static Optional<SocialType> getValue(String socialTypeString) {
        return Arrays.stream(values())
                .filter(socialType -> socialType.name().equalsIgnoreCase(socialTypeString))
                .findFirst();
    }
}
