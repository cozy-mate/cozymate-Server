package com.cozymate.cozymate_server.domain.rule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.dto.response.RuleDetailResponseDTO;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepositoryService;
import com.cozymate.cozymate_server.fixture.MateFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.RoomFixture;
import com.cozymate.cozymate_server.fixture.RuleFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import java.util.List;
import java.util.Optional;
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
public class RuleQueryServiceTest {

    @Mock
    private MateRepository mateRepository;

    @Mock
    private RuleRepositoryService ruleRepositoryService;

    @InjectMocks
    private RuleQueryService ruleQueryService;

    @Nested
    class getRule {

        Member member;
        Room room;
        Mate mate;
        Rule rule;
        Rule rule2;

        @BeforeEach
        void setup() {
            // given
            member = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
            room = RoomFixture.정상_1(member);
            mate = MateFixture.정상_1(room, member);
            rule = RuleFixture.정상_1(room);
            rule2 = RuleFixture.정상_2(room);

            given(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(
                any(Long.class), any(Long.class), any(EntryStatus.class)))
                .willReturn(Optional.of(mate));
        }

        @Test
        @DisplayName("특정 방의 Rule 목록을 조회한다.")
        void success() {
            // given
            given(ruleRepositoryService.getRuleListByRoomId(any(Long.class)))
                .willReturn(List.of(rule, rule2));
            // when
            List<RuleDetailResponseDTO> ruleDetailResponseDTOList = ruleQueryService.getRule(
                member, room.getId());
            // then
            assertThat(ruleDetailResponseDTOList).hasSize(2);
            assertThat(ruleDetailResponseDTOList.get(0).ruleId()).isEqualTo(rule.getId());
            assertThat(ruleDetailResponseDTOList.get(1).ruleId()).isEqualTo(rule2.getId());
            assertThat(ruleDetailResponseDTOList.get(0).content()).isEqualTo(rule.getContent());
            assertThat(ruleDetailResponseDTOList.get(0).memo()).isEqualTo(rule.getMemo());
            assertThat(ruleDetailResponseDTOList.get(1).content()).isEqualTo(rule2.getContent());
            assertThat(ruleDetailResponseDTOList.get(1).memo()).isEqualTo(rule2.getMemo());
        }

        @Test
        @DisplayName("특정 방의 비어있는 Rule 목록을 조회한다.")
        void success_when_empty() {
            // given
            given(ruleRepositoryService.getRuleListByRoomId(any(Long.class)))
                .willReturn(List.of());
            // when
            List<RuleDetailResponseDTO> ruleDetailResponseDTOList = ruleQueryService.getRule(
                member, room.getId());
            // then
            assertThat(ruleDetailResponseDTOList).hasSize(0);
        }

    }
}
