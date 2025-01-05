package com.cozymate.cozymate_server.data;

import com.cozymate.cozymate_server.domain.university.University;

public class TestUniversity {

    static String NAME = "테스트대학교";
    static String MAIL_PATTERN = "gmail.com";
    public static University createTestUniversity() {
        return University.builder()
                .name(NAME)
                .mailPattern(MAIL_PATTERN)
                .build();
    }
}