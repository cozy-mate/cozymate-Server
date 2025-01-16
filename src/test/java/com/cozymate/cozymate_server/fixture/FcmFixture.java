package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.member.Member;

@SuppressWarnings("NonAsciiCharacters")
public class FcmFixture {

    // 정상 더미데이터, isValid가 true인 경우
    public Fcm 정상_1(Member member) {
        return Fcm.builder()
            .id("test_device_id_1")
            .token("test_token_value_1")
            .member(member)
            .isValid(true)
            .build();
    }

    // 정상 더미데이터, isValid가 true인 경우
    public Fcm 정상_2(Member member) {
        return Fcm.builder()
            .id("test_device_id_2")
            .token("test_token_value_2")
            .member(member)
            .isValid(true)
            .build();
    }

    // 정상 더미데이터, isValid가 false인 경우
    public Fcm 정상_3(Member member) {
        return Fcm.builder()
            .id("test_device_id_3")
            .token("test_token_value_3")
            .member(member)
            .isValid(false)
            .build();
    }

    // 정상 더미데이터, isValid가 false인 경우
    public Fcm 정상_4(Member member) {
        return Fcm.builder()
            .id("test_device_id_4")
            .token("test_token_value_4")
            .member(member)
            .isValid(false)
            .build();
    }
}
