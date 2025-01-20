package com.cozymate.cozymate_server.domain.rule.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.dto.response.RuleDetailResponseDTO;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepository;
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
    private RuleRepository ruleRepository;
    @Mock
    private MateRepository mateRepository;

    @InjectMocks
    private RuleQueryService ruleQueryService;

    @Nested
    class getRule {

        private Room room;
        private Member member;
        private Mate mate;

        private List<Rule> ruleList;


        @BeforeEach
        void setUp() {
            // given : createRule 테스트 전체에서 공통으로 쓰이는 데이터 준비
            member = MemberFixture.정상_1(UniversityFixture.createTestUniversity()); // 기본 회원
            room = RoomFixture.정상_1(member); // 기본 방
            mate = MateFixture.정상_1(room, member); // 기본 방 멤버

            given(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), member.getId(),
                EntryStatus.JOINED))
                .willReturn(Optional.ofNullable(mate)); // 방 멤버 조회
        }

        @Test
        @DisplayName("특정 방의 Rule 목록을 정상적으로 가져온다.")
        void success_get_rule() {
            // given : 특정 방의 Rule 목록을 가져오는데 필요한 데이터
            ruleList = RuleFixture.정상_List(5, room); // 기본 방 규칙 목록
            given(ruleRepository.findAllByRoomId(room.getId()))
                .willReturn(ruleList); // 방의 규칙 목록 조회

            // when : 특정 방의 Rule 목록을 가져오는 서비스 호출
            List<RuleDetailResponseDTO> responseDto = ruleQueryService.getRule(member,
                room.getId());

            // then : 특정 방의 Rule 목록을 정상적으로 가져온다.
            assertThat(responseDto).isNotNull();
            assertThat(responseDto.size()).isEqualTo(ruleList.size());
            for (int i = 0; i < ruleList.size(); i++) {
                Rule rule = ruleList.get(i);
                RuleDetailResponseDTO ruleDetailResponseDTO = responseDto.get(i);
                assertThat(ruleDetailResponseDTO.ruleId()).isEqualTo(rule.getId());
                assertThat(ruleDetailResponseDTO.content()).isEqualTo(rule.getContent());
                assertThat(ruleDetailResponseDTO.memo()).isEqualTo(rule.getMemo());
            }

        }

        @Test
        @DisplayName("비어있을 때에도 빈 배열을 반환한다.")
        void success_get_rule_when_rule_list_is_empty() {
            // given : 특정 방의 Rule 목록을 가져오는데 필요한 데이터
            ruleList = List.of(); // 방에 규칙 목록이 비었을 때
            given(ruleRepository.findAllByRoomId(room.getId()))
                .willReturn(ruleList); // 방의 규칙 목록 조회

            // when : 특정 방의 Rule 목록을 가져오는 서비스 호출
            List<RuleDetailResponseDTO> responseDto = ruleQueryService.getRule(member,
                room.getId());

            // then : 빈 배열을 반환한다.
            assertThat(responseDto.size()).isEqualTo(0);
        }

    }

}
