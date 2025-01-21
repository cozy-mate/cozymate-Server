package com.cozymate.cozymate_server.domain.member;


import com.cozymate.cozymate_server.fixture.MemberFixture;

import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.member.service.MemberCommandService;
import com.cozymate.cozymate_server.domain.member.service.MemberQueryService;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.domain.university.repository.UniversityRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
class MemberCommandServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private UniversityRepository universityRepository;

    @Mock
    private MemberQueryService memberQueryService;
    @InjectMocks
    private MemberCommandService memberCommandService;

    private Member testMember;
    private Member testMember2;

    private static final String VALID_NICKNAME = "validNick";


    MemberCommandServiceTest() {
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        University university = UniversityFixture.createTestUniversity();
        universityRepository.save(university);

        when(universityRepository.save(university)).thenReturn(university);
        when(universityRepository.findById(anyLong())).thenReturn(Optional.of(university));

        // createAndSaveTestMembers 호출로 테스트용 멤버 생성
        List<Member> members = MemberFixture.정상_남성_리스트(university,
            2);

        testMember = members.get(0); // 첫 번째로 생성된 멤버를 사용
        testMember2 = members.get(1);

        // memberRepository에서 findById와 save 메서드를 Mock 설정
        when(memberRepository.findById(testMember.getId())).thenReturn(Optional.of(testMember));
        when(memberRepository.save(testMember)).thenReturn(testMember);
    }

    @Test
    void testUpdateNickname_Success() {
        // given
        String newNickname = "newTestUser";
        // memberQueryService의 isValidNickName을 mock 처리
        doNothing().when(memberQueryService).isValidNickName(VALID_NICKNAME);  // 유효성 검증 성공

        // when
        memberCommandService.updateNickname(testMember, newNickname);

        // then
        assertThat(testMember.getNickname()).isEqualTo(newNickname);
        verify(memberRepository, times(1)).findById(testMember.getId());
    }

    @Test
    void testUpdateNickname_Failure_NicknameExists() {
        // given
        // 이미 생성된 testMember2의 닉네임을 사용
        String duplicateNickname = testMember2.getNickname();
        // memberQueryService의 isValidNickName mock 처리
        doThrow(new GeneralException(ErrorStatus._NICKNAME_EXISTING))
            .when(memberQueryService)
            .isValidNickName(duplicateNickname);  // duplicateNickname으로 예외 던지기
        // when & then
        assertThatThrownBy(() -> memberCommandService.updateNickname(testMember, duplicateNickname))
            .isInstanceOf(GeneralException.class);  // 예외 클래스 확인
    }

        @Test
        void testUpdatePersona_Success () {
            // given
            int newPersona = 10;

            // when
            memberCommandService.updatePersona(testMember, newPersona);

            // then
            assertThat(testMember.getPersona()).isEqualTo(newPersona);
            verify(memberRepository, times(1)).findById(testMember.getId());
        }

        @Test
        void testUpdateBirthday_Success () {
            // given
            LocalDate newBirthday = LocalDate.of(1999, 12, 31);

            // when
            memberCommandService.updateBirthday(testMember, newBirthday);

            // then
            assertThat(testMember.getBirthDay()).isEqualTo(newBirthday);
            verify(memberRepository, times(1)).findById(testMember.getId());
        }

        @Test
        void testUpdateMajor_Success () {
            // given
            String newMajor = "전자공학과";

            // when
            memberCommandService.updateMajor(testMember, newMajor);

            // then
            assertThat(testMember.getMajorName()).isEqualTo(newMajor);
            verify(memberRepository, times(1)).findById(testMember.getId());
        }

    }
