package com.cozymate.cozymate_server.domain.rule.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.rule.Rule;
import com.cozymate.cozymate_server.domain.rule.dto.request.CreateRuleRequestDTO;
import com.cozymate.cozymate_server.domain.rule.dto.response.RuleIdResponseDTO;
import com.cozymate.cozymate_server.domain.rule.repository.RuleRepository;
import com.cozymate.cozymate_server.fixture.RuleFixture;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class RuleCommandServiceTest {

    private static final RuleFixture ruleFixture = new RuleFixture();

    @Mock
    private RuleRepository ruleRepository;
    @Mock
    private MateRepository mateRepository;

    @InjectMocks
    private RuleCommandService ruleCommandService;

    @Nested
    class createRule {

        private Room room;
        private Member member;
        private Mate mate;
        private CreateRuleRequestDTO requestDto;
        private Rule rule;

        @BeforeEach
        void setUp() {
            // given : Rule 테스트 전체에서 공통으로 쓰이는 데이터 준비
            room = Room.builder().id(1L).build(); // TODO: 기본 방 -> Fixture로 대체
            member = Member.builder().id(1L).socialType(SocialType.KAKAO).role(Role.USER_VERIFIED)
                .clientId("qwer:KAKAO").nickname("무빗").gender(Gender.MALE).birthDay(LocalDate.MAX)
                .persona(1).build(); // TODO: 기본 멤버 -> Fixture로 대체
            mate = Mate.builder().id(1L).room(room).member(member)
                .entryStatus(EntryStatus.JOINED).build(); // TODO: 기본 방 멤버 -> Fixture로 대체

            when(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(room.getId(), member.getId(),
                EntryStatus.JOINED)).thenReturn(Optional.ofNullable(mate)); // 방 멤버 조회

            rule = ruleFixture.규칙_1(room); // 생성되었을 때 사용할 규칙
            requestDto = ruleFixture.규칙_1_생성_요청_DTO(); // 기본 규칙 생성 요청 DTO
            lenient().when(ruleRepository.save(any()))
                .thenReturn(rule); // 규칙 생성
        }

        @Test
        void 기존규칙이_없을때_성공한다() {
            // given
            when(ruleRepository.countAllByRoomId(room.getId())).thenReturn(0); // 기존에 규칙이 없는 경우

            // when
            RuleIdResponseDTO responseDto = ruleCommandService.createRule(member, room.getId(),
                requestDto);

            // then
            assertThat(responseDto.ruleId()).isEqualTo(ruleFixture.규칙_1(room).getId());

        }

        @Test
        void 기존규칙이_9개일때_성공한다() {
            // given
            when(ruleRepository.countAllByRoomId(room.getId())).thenReturn(9); // 기존에 규칙이 9개인 경우

            // when
            RuleIdResponseDTO responseDto = ruleCommandService.createRule(member, room.getId(),
                requestDto);

            // then
            assertThat(responseDto.ruleId()).isEqualTo(ruleFixture.규칙_1(room).getId());

        }

        @Test
        void 기존규칙이_10개일때_실패한다() {
            // given
            when(ruleRepository.countAllByRoomId(room.getId())).thenReturn(
                10); // 기존에 규칙이 10개인 경우

            // when, then
            assertThatThrownBy(
                () -> ruleCommandService.createRule(member, room.getId(), requestDto))
                .isInstanceOf(GeneralException.class);
        }
    }
}
