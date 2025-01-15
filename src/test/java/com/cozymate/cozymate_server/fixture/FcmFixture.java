package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.fcm.Fcm;
import com.cozymate.cozymate_server.domain.member.Member;

public class FcmFixture {

    private static final String DEVICE_ID_1 = "test_device_id_1";
    private static final String DEVICE_ID_2 = "test_device_id_2";
    private static final String DEVICE_ID_3 = "test_device_id_3";
    private static final String DEVICE_ID_4 = "test_device_id_4";

    private static final String TOKEN_VALUE_1 = "test_token_value_1";
    private static final String TOKEN_VALUE_2 = "test_token_value_2";
    private static final String TOKEN_VALUE_3 = "test_token_value_3";
    private static final String TOKEN_VALUE_4 = "test_token_value_4";

    public static Fcm buildValidFcm1(Member member) {
        return Fcm.builder()
            .id(DEVICE_ID_1)
            .token(TOKEN_VALUE_1)
            .member(member)
            .isValid(true)
            .build();
    }

    public static Fcm buildValidFcm2(Member member) {
        return Fcm.builder()
            .id(DEVICE_ID_2)
            .token(TOKEN_VALUE_2)
            .member(member)
            .isValid(true)
            .build();
    }

    public static Fcm buildInvalidFcm1(Member member) {
        return Fcm.builder()
            .id(DEVICE_ID_3)
            .token(TOKEN_VALUE_3)
            .member(member)
            .isValid(false)
            .build();
    }

    public static Fcm buildInvalidFcm2(Member member) {
        return Fcm.builder()
            .id(DEVICE_ID_4)
            .token(TOKEN_VALUE_4)
            .member(member)
            .isValid(false)
            .build();
    }

}
