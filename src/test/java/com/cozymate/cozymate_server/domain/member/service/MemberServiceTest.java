package com.cozymate.cozymate_server.domain.member.service;


import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.domain.member.validator.MemberValidator;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepositoryService;
import com.cozymate.cozymate_server.fixture.MemberFixture;

import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private UniversityRepositoryService universityRepositoryService;

    @Mock
    private MemberRepositoryService memberRepositoryService;

    @Mock
    private MemberValidator memberValidator;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private Member testMember2;

    private static final String VALID_NICKNAME = "validNick";

    @BeforeEach
    void setUp() {
        University university = UniversityFixture.createTestUniversity();
        universityRepositoryService.createUniversity(university);

        when(universityRepositoryService.createUniversity(university)).thenReturn(university);

        List<Member> members = MemberFixture.정상_남성_리스트(university, 2);
        testMember = members.get(0);
        testMember2 = members.get(1);

        when(memberRepositoryService.getMemberByIdOrThrow(testMember.getId())).thenReturn(testMember);
        when(memberRepositoryService.createMember(testMember)).thenReturn(testMember);
    }
}
