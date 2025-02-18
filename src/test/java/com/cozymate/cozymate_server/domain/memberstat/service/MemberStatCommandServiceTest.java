package com.cozymate.cozymate_server.domain.memberstat.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.cozymate.cozymate_server.domain.member.Member;

import com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.service.LifestyleMatchRateService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepository;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.service.MemberStatCommandService;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.MemberStatFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class MemberStatCommandServiceTest {

    @Mock
    private LifestyleMatchRateService lifestyleMatchRateService;

    @Mock
    private MemberStatRepository memberStatRepository;

    @InjectMocks
    private MemberStatCommandService memberStatCommandService;

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class createMemberStat {

        private Member member;
        private MemberStat memberStat;

        @BeforeEach
        void setUp() {
            member = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
            memberStat = MemberStatFixture.정상_1(member);

            // `save()`가 저장된 `memberStat`을 반환하도록 설정
            given(memberStatRepository.save(any(MemberStat.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

            // `lifestyleMatchRateService.saveLifeStyleMatchRate()` 실행 시 예외 발생하지 않도록 설정
            doNothing().when(lifestyleMatchRateService)
                .saveLifeStyleMatchRate(any(MemberStat.class));
        }

        @Test
        @DisplayName("유효한 요청 dto인 경우 성공")
        void success_when_valid_dto() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.정상_생성_요청_DTO();

            // when
            Long memberId = memberStatCommandService.createMemberStat(member, requestDTO);

            // then
            assertThat(memberId).isEqualTo(member.getId());
            verify(memberStatRepository, times(1)).save(any(MemberStat.class)); // 저장 메서드 호출 확인
            verify(lifestyleMatchRateService, times(1)).saveLifeStyleMatchRate(
                any(MemberStat.class)); // 라이프스타일 매치율 저장 확인
        }

        @Test
        @DisplayName("학번이 숫자로 변환할 수 없는 값일 경우 예외 발생")
        void failure_when_invalid_admissionYear_format() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.실패_잘못된_학번_입력();

            // when & then
            assertThatThrownBy(() -> MemberStatConverter.toEntity(member, requestDTO))
                .isInstanceOf(NumberFormatException.class);
        }

        @Test
        @DisplayName("smoking 값이 존재하지 않는 경우 예외 발생")
        void failure_when_invalid_smoking_value() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.실패_잘못된_smoking();

            // when & then
            assertThatThrownBy(() -> MemberStatConverter.toEntity(member, requestDTO))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR.getMessage());
        }

        @Test
        @DisplayName("lifePattern 값이 존재하지 않는 경우 예외 발생")
        void failure_when_invalid_lifePattern_value() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.실패_잘못된_lifePattern();

            // when & then
            assertThatThrownBy(() -> MemberStatConverter.toEntity(member, requestDTO))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR.getMessage());
        }

        @Test
        @DisplayName("mbti 값이 존재하지 않는 경우 예외 발생")
        void failure_when_invalid_mbti_value() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.실패_잘못된_mbti();

            // when & then
            assertThatThrownBy(() -> MemberStatConverter.toEntity(member, requestDTO))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR.getMessage());
        }
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class modifyMemberStat {

        private Member member;
        private MemberStat memberStat;

        @BeforeEach
        void setUp() {
            member = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
            memberStat = MemberStatFixture.정상_1(member);

            // `findByMemberId()`가 저장된 `memberStat`을 반환하도록 설정
            given(memberStatRepository.findByMemberId(member.getId()))
                .willReturn(java.util.Optional.of(memberStat));

            // `saveLifeStyleMatchRate()` 실행 시 예외 발생하지 않도록 설정
            doNothing().when(lifestyleMatchRateService)
                .saveLifeStyleMatchRate(any(MemberStat.class));
        }

        @Test
        @DisplayName("유효한 요청 DTO인 경우 성공")
        void success_when_valid_dto() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.정상_생성_요청_DTO();

            // when
            Long memberId = memberStatCommandService.modifyMemberStat(member, requestDTO);

            // then
            assertThat(memberId).isEqualTo(member.getId());
            assertThat(memberStat.getSelfIntroduction()).isEqualTo(requestDTO.selfIntroduction());
            verify(lifestyleMatchRateService, times(1)).saveLifeStyleMatchRate(memberStat);
        }

        @Test
        @DisplayName("수정할 MemberStat이 존재하지 않는 경우 예외 발생")
        void failure_when_memberStat_not_exists() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.정상_생성_요청_DTO();
            given(memberStatRepository.findByMemberId(member.getId()))
                .willReturn(java.util.Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberStatCommandService.modifyMemberStat(member, requestDTO))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus._MEMBERSTAT_NOT_EXISTS.getMessage());
        }

        @Test
        @DisplayName("학번이 숫자로 변환할 수 없는 값일 경우 예외 발생")
        void failure_when_invalid_admissionYear_format() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.실패_잘못된_학번_입력();

            // when & then
            assertThatThrownBy(() -> memberStatCommandService.modifyMemberStat(member, requestDTO))
                .isInstanceOf(NumberFormatException.class);
        }

        @Test
        @DisplayName("smoking 값이 존재하지 않는 경우 예외 발생")
        void failure_when_invalid_smoking_value() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.실패_잘못된_smoking();

            // when & then
            assertThatThrownBy(() -> memberStatCommandService.modifyMemberStat(member, requestDTO))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR.getMessage());
        }

        @Test
        @DisplayName("lifePattern 값이 존재하지 않는 경우 예외 발생")
        void failure_when_invalid_lifePattern_value() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.실패_잘못된_lifePattern();

            // when & then
            assertThatThrownBy(() -> memberStatCommandService.modifyMemberStat(member, requestDTO))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR.getMessage());
        }

        @Test
        @DisplayName("mbti 값이 존재하지 않는 경우 예외 발생")
        void failure_when_invalid_mbti_value() {
            // given
            CreateMemberStatRequestDTO requestDTO = MemberStatFixture.실패_잘못된_mbti();

            // when & then
            assertThatThrownBy(() -> memberStatCommandService.modifyMemberStat(member, requestDTO))
                .isInstanceOf(GeneralException.class)
                .hasMessageContaining(ErrorStatus._MEMBERSTAT_FILE_READ_ERROR.getMessage());
        }
    }


}
