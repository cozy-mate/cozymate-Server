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

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class UpdateNickname {

        @Test
        @DisplayName("닉네임이 유효할 때 성공한다")
        void success_when_nickname_is_valid() {
            // given
            String newNickname = "newTestUser";
            doNothing().when(memberValidator).checkNickname(newNickname);

            // when
            memberService.updateNickname(testMember, newNickname);

            // then
            assertThat(testMember.getNickname()).isEqualTo(newNickname);
            verify(memberRepositoryService, times(1)).getMemberByIdOrThrow(testMember.getId());
        }

        @Test
        @DisplayName("닉네임이 중복될 때 실패한다")
        void failure_when_nickname_exists() {
            // given
            String duplicateNickname = testMember2.getNickname();
            doThrow(new GeneralException(ErrorStatus._NICKNAME_EXISTING))
                .when(memberValidator)
                .checkNickname(duplicateNickname);

            // when & then
            assertThatThrownBy(() -> memberService.updateNickname(testMember, duplicateNickname))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class UpdatePersona {

        @Test
        @DisplayName("새로운 Persona 값으로 성공적으로 업데이트된다")
        void success_when_persona_updated() {
            // given
            int newPersona = 10;

            // when
            memberService.updatePersona(testMember, newPersona);

            // then
            assertThat(testMember.getPersona()).isEqualTo(newPersona);
            verify(memberRepositoryService, times(1)).getMemberByIdOrThrow(testMember.getId());
        }
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class UpdateBirthday {

        @Test
        @DisplayName("새로운 생년월일로 성공적으로 업데이트된다")
        void success_when_birthday_updated() {
            // given
            LocalDate newBirthday = LocalDate.of(1999, 12, 31);

            // when
            memberService.updateBirthday(testMember, newBirthday);

            // then
            assertThat(testMember.getBirthDay()).isEqualTo(newBirthday);
            verify(memberRepositoryService, times(1)).getMemberByIdOrThrow(testMember.getId());
        }
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class UpdateMajor {

        @Test
        @DisplayName("새로운 전공명으로 성공적으로 업데이트된다")
        void success_when_major_updated() {
            // given
            String newMajor = "전자공학과";

            // when
            memberService.updateMajor(testMember, newMajor);

            // then
            assertThat(testMember.getMajorName()).isEqualTo(newMajor);
            verify(memberRepositoryService, times(1)).getMemberByIdOrThrow(testMember.getId());
        }
    }
}
