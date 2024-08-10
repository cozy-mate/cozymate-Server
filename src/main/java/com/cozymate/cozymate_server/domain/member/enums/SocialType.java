package com.cozymate.cozymate_server.domain.member.enums;

public enum SocialType {
    KAKAO,
    NAVER,
    GOOGLE,
    APPLE
    ;
    // ex. return KAKAO;
    @Override
    public String toString() {
        return name();
    }
}
