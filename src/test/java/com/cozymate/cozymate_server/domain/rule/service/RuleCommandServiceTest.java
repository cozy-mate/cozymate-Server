package com.cozymate.cozymate_server.domain.rule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

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
        willDoNothing().given(ruleValidator).checkRuleMaxLimit(any(Long.class));
        willDoNothing().given(ruleValidator).checkUpdatePermission(any(Mate.class), any(Rule.class));
    }


    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
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
        @DisplayName("기존 규칙이 없을 때 Rule을 생성한다")
        void success_when_existed_rule_count_0() {
            // given
            given(ruleRepositoryService.getRuleCountByRoomId(room.getId()))
                .willReturn(0); // 기존에 규칙이 없는 경우

            // when
            RuleIdResponseDTO responseDto = ruleCommandService.createRule(member, room.getId(),
                requestDto);

            // then
            assertThat(responseDto.ruleId()).isEqualTo(rule.getId());

        }

        @Test
        @DisplayName("기존 규칙이 9개일 때 Rule을 생성한다")
        void success_when_existed_rule_count_9() {
            // given
            given(ruleRepositoryService.getRuleCountByRoomId(room.getId()))
                .willReturn(9); // 기존에 규칙이 9개인 경우

            // when
            RuleIdResponseDTO responseDto = ruleCommandService.createRule(member, room.getId(),
                requestDto);

            // then
            assertThat(responseDto.ruleId()).isEqualTo(rule.getId());

        }

//        @Test
//        @DisplayName("기존 규칙이 10개일 때 Rule 최대 제한에 걸린다")
//        void failure_when_existed_rule_over_10() {
//            // given
//            given(ruleRepositoryService.getRuleCountByRoomId(room.getId()))
//                .willReturn(10); // 기존에 규칙이 10개인 경우
//
//            // when, then
//            assertThatThrownBy(
//                () -> ruleCommandService.createRule(member, room.getId(), requestDto))
//                .isInstanceOf(GeneralException.class);
//        }
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class deleteRule {

        @BeforeEach
        void setUp() {

            given(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), member.getId(),
                EntryStatus.JOINED)).willReturn(Optional.of(mate)); // 방 멤버 조회
        }

        @Test
        @DisplayName("존재하는 규칙을 삭제할 때 삭제된다")
        void success_when_rule_exist() {
            // given
            given(ruleRepositoryService.getRuleOrThrow(rule.getId()))
                .willReturn(rule); // 규칙 조회

            // when
            ruleCommandService.deleteRule(member, room.getId(), rule.getId());
            // then void
        }

//        @Test
//        @DisplayName("Rule의 방 정보가 Room과 일치하지 않으면 실패한다.")
//        void failure_when_member_not_in_room() {
//            // given
//            Member member2 = MemberFixture.정상_2(UniversityFixture.createTestUniversity()); // 다른 멤버
//            Room room2 = RoomFixture.정상_2(member2); // 다른 방
//            Rule rule2 = RuleFixture.정상_1(room2); // 생성되었을 때 사용할 규칙
//
//            given(ruleRepositoryService.getRuleOrThrow(rule2.getId()))
//                .willReturn(rule2); // 규칙 조회
//
//            // when, then
//            assertThatThrownBy(
//                () -> ruleCommandService.deleteRule(member, room.getId(), rule2.getId()))
//                .isInstanceOf(GeneralException.class);
//        }
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class updateRule {

        private CreateRuleRequestDTO requestDto;

        @BeforeEach
        void setUp() {
            requestDto = RuleFixture.정상_2_생성_요청_DTO(); // 정상_1 -> 정상_2로 변경

            given(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), member.getId(),
                EntryStatus.JOINED)).willReturn(Optional.of(mate)); // 방 멤버 조회
        }

        @Test
        @DisplayName("규칙이 존재하고, 멤버가 방에 속할 때 성공한다.")
        void success_when_rule_exist() {
            // given
            given(ruleRepositoryService.getRuleOrThrow(rule.getId()))
                .willReturn(rule); // 규칙 조회

            // when
            ruleCommandService.updateRule(member, room.getId(), rule.getId(), requestDto);

            // then
            assertThat(rule.getContent()).isEqualTo(requestDto.content());
            assertThat(rule.getMemo()).isEqualTo(requestDto.memo());
        }

//        @Test
//        @DisplayName("Rule의 방 정보가 Room과 일치하지 않으면 실패한다.")
//        void failure_when_member_not_in_room() {
//            // given
//            Member member2 = MemberFixture.정상_2(UniversityFixture.createTestUniversity()); // 다른 멤버
//            Room room2 = RoomFixture.정상_2(member2); // 다른 방
//            Rule rule2 = RuleFixture.정상_1(room2); // 생성되었을 때 사용할 규칙
//
//            given(ruleRepositoryService.getRuleOrThrow(rule2.getId()))
//                .willReturn(rule2); // 규칙 조회
//
//            // when, then
//            assertThatThrownBy(
//                () -> ruleCommandService.updateRule(member, room.getId(), rule2.getId(),
//                    requestDto))
//                .isInstanceOf(GeneralException.class);
//        }
    }
}
