package com.cozymate.cozymate_server.domain.rule.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepositoryService;
import com.cozymate.cozymate_server.fixture.MateFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.RoomFixture;
import com.cozymate.cozymate_server.fixture.RuleFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class RuleValidatorTest {

    @Mock
    private RuleRepositoryService ruleRepositoryService;

    @InjectMocks
    private RuleValidator ruleValidator;

    @BeforeEach
    void setup() {

    }

    @Nested
    class checkRuleMaxLimit {

        @Test
        @DisplayName("Count가 제한 미만이면 검증에 성공한다.")
        void success_when_count_is_under_max_limit() {
            // given
            given(ruleRepositoryService.getRuleCountByRoomId(any(Long.class)))
                .willReturn(9);

            // when, then
            assertThatCode(() -> ruleValidator.checkRuleMaxLimit(1L))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Count가 제한된 값이면 예외를 발생한다.")
        void failure_when_count_is_max_limit() {
            // given
            given(ruleRepositoryService.getRuleCountByRoomId(any(Long.class)))
                .willReturn(10);

            // when, then
            assertThatThrownBy(
                () -> ruleValidator.checkRuleMaxLimit(1L))
                .isInstanceOf(GeneralException.class);
        }

        @Test
        @DisplayName("Count가 제한된 값을 초과하면 예외를 발생한다.")
        void failure_when_count_is_over_max_limit() {
            // given
            given(ruleRepositoryService.getRuleCountByRoomId(any(Long.class)))
                .willReturn(11);

            // when, then
            assertThatThrownBy(
                () -> ruleValidator.checkRuleMaxLimit(1L))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    class checkUpdatePermission {

        Mate mate;
        Rule rule;
        Rule rule2;

        @BeforeEach
        void setUp() {
            Member member = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
            Member member2 = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
            Room room = RoomFixture.정상_1(member);
            Room room2 = RoomFixture.정상_2(member2);
            mate = MateFixture.정상_1(room, member);
            rule = RuleFixture.정상_1(room);
            rule2 = RuleFixture.정상_1(room2);
        }

        @Test
        @DisplayName("Mate가 Rule이 속한 방에 있으면 검증에 성공한다.")
        void success_when_mate_is_in_room() {
            // given

            // when, then
            assertThatCode(() -> ruleValidator.checkUpdatePermission(mate, rule))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Mate가 Rule이 속한 방에 없으면 예외를 발생한다.")
        void failure_when_mate_is_in_other_room() {
            // given

            // when, then
            assertThatThrownBy(
                () -> ruleValidator.checkUpdatePermission(mate, rule2))
                .isInstanceOf(GeneralException.class);
        }

    }

}
