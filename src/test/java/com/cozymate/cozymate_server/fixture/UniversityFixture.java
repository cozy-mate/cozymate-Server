package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.university.University;
import java.util.List;


@SuppressWarnings("NonAsciiCharacters")
public class UniversityFixture {
    public static University createTestUniversity() {
        return University.builder()
                .name("테스트대학교")
                .mailPatterns(List.of("gmail.com"))
                .build();
    }
}