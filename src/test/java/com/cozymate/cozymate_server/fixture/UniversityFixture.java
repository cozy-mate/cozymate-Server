package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.university.University;


@SuppressWarnings("NonAsciiCharacters")
public class UniversityFixture {
    public static University createTestUniversity() {
        return University.builder()
                .name("테스트대학교")
                .mailPattern("gmail.com")
                .build();
    }
}