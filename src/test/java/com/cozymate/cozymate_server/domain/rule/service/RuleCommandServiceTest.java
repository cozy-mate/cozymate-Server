package com.cozymate.cozymate_server.domain.rule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.dto.request.CreateRuleRequestDTO;
import com.cozymate.cozymate_server.domain.rule.dto.response.RuleIdResponseDTO;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepositoryService;
import com.cozymate.cozymate_server.domain.rule.validator.RuleValidator;
import com.cozymate.cozymate_server.fixture.MateFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.RoomFixture;
import com.cozymate.cozymate_server.fixture.RuleFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Optional;
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
public class RuleCommandServiceTest {

    @Mock
    private RuleRepositoryService ruleRepositoryService;
    @Mock
    private MateRepository mateRepository;
    @Mock
    private RuleValidator ruleValidator;
    @InjectMocks
    private RuleCommandService ruleCommandService;

    // 해당 테스트에서 공통으로 사용할 데이터
    private Member member;
    private Room room;
    private Mate mate;
    private Rule rule;

    @BeforeEach
    void setup() {

        member = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
        room = RoomFixture.정상_1(member);
        mate = MateFixture.정상_1(room, member);
        rule = RuleFixture.정상_1(room);

        given(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), mate.getId(),
            EntryStatus.JOINED))
            .willReturn(Optional.of(mate));
    }


    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT) // 규칙 수정에서는 권한 검증만 테스트하므로 느슨한 검증 설정
    class createRule {

        private CreateRuleRequestDTO requestDto;

        @BeforeEach
        void setUp() {
            requestDto = RuleFixture.정상_1_생성_요청_DTO(); // 기본 규칙 생성 요청 DTO
            // 규칙 저장은 항상 성공적으로 실행될 것으로 가정
            given(ruleRepositoryService.createRule(
                any(Rule.class))) // rule은 requestDto에서 만들어진 Rule과 다른 주소의 객체임
                .willReturn(rule); // 규칙 저장
        }

        @Test
        @DisplayName("최대 개수 제한에 걸리지 않으면 생성한다")
        void success_when_under_rule_max_limit() {
            // given - 최대 개수 제한 예외 발생하지 않음
            willDoNothing()
                .given(ruleValidator).checkRuleMaxLimit(any(Long.class));

            // when
            RuleIdResponseDTO responseDto = ruleCommandService.createRule(member, room.getId(),
                requestDto);

            // then
            assertThat(responseDto.ruleId()).isEqualTo(rule.getId());

        }

        @Test
        @DisplayName("최대 개수 제한에 걸리면 에러가 발생한다.")
        void failure_when_over_rule_max_limit() {
            // given - 최대 개수 제한 예외 발생
            willThrow(new GeneralException(ErrorStatus._RULE_MAX_LIMIT))
                .given(ruleValidator).checkRuleMaxLimit(any(Long.class));

            // when, then
            assertThatThrownBy(
                () -> ruleCommandService.createRule(member, room.getId(), requestDto))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    class deleteRule {

        @BeforeEach
        void setUp() {

            given(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), member.getId(),
                EntryStatus.JOINED)).willReturn(Optional.of(mate)); // 방 멤버 조회
        }

        @Test
        @DisplayName("권한이 있는 Mate가 규칙을 삭제할 수 있다")
        void success_when_has_permission() {
            // given
            given(ruleRepositoryService.getRuleOrThrow(rule.getId()))
                .willReturn(rule); // 규칙 조회
            // Mate가 Rule에 대한 권한을 가짐
            willDoNothing().given(ruleValidator)
                .checkUpdatePermission(any(Mate.class), any(Rule.class));

            // when
            ruleCommandService.deleteRule(member, room.getId(), rule.getId());
            // then void
        }

        @Test
        @DisplayName("권한이 없는 Mate가 규칙을 삭제할 수 없다.")
        void failure_when_has_not_permission() {
            // given
            given(ruleRepositoryService.getRuleOrThrow(rule.getId()))
                .willReturn(rule); // 규칙 조회
            // Mate가 Rule에 대한 권한을 가지지 않음
            willThrow(new GeneralException(ErrorStatus._RULE_PERMISSION_DENIED))
                .given(ruleValidator).checkUpdatePermission(any(Mate.class), any(Rule.class));

            // when, then
            assertThatThrownBy(
                () -> ruleCommandService.deleteRule(member, room.getId(), rule.getId()))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT) // 규칙 수정에서는 권한 검증만 테스트하므로 느슨한 검증 설정
    class updateRule {

        private CreateRuleRequestDTO requestDto;

        @BeforeEach
        void setUp() {
            requestDto = RuleFixture.정상_2_생성_요청_DTO(); // 정상_1 -> 정상_2로 변경

            given(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), member.getId(),
                EntryStatus.JOINED)).willReturn(Optional.of(mate)); // 방 멤버 조회
        }

        @Test
        @DisplayName("권한을 가진 Mate가 규칙을 수정할 수 있다.")
        void success_when_has_permission() {
            // given
            given(ruleRepositoryService.getRuleOrThrow(rule.getId()))
                .willReturn(rule); // 규칙 조회
            // Mate가 Rule에 대한 권한을 가짐
            willDoNothing().given(ruleValidator)
                .checkUpdatePermission(any(Mate.class), any(Rule.class));

            // when
            ruleCommandService.updateRule(member, room.getId(), rule.getId(), requestDto);

            // then
            assertThat(rule.getContent()).isEqualTo(requestDto.content());
            assertThat(rule.getMemo()).isEqualTo(requestDto.memo());
        }

        @Test
        @DisplayName("권한을 가지지 않은 Mate는 규칙을 수정할 수 없다.")
        void failure_when_has_not_permission() {
            // given
            given(ruleRepositoryService.getRuleOrThrow(rule.getId()))
                .willReturn(rule); // 규칙 조회
            willThrow(new GeneralException(ErrorStatus._RULE_PERMISSION_DENIED))
                .given(ruleValidator).checkUpdatePermission(any(Mate.class), any(Rule.class));

            // when, then
            assertThatThrownBy(
                () -> ruleCommandService.updateRule(member, room.getId(), rule.getId(),
                    requestDto))
                .isInstanceOf(GeneralException.class);
        }
    }
}
