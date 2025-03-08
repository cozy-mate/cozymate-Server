package com.cozymate.cozymate_server.domain.role.validator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepositoryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.fixture.MateFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.RoleFixture;
import com.cozymate.cozymate_server.fixture.RoomFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
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
public class RoleValidatorTest {

    @Mock
    private RoleRepositoryService roleRepositoryService;
    @Mock
    private MateRepository mateRepository;

    @InjectMocks
    private RoleValidator roleValidator;

    private Room room;
    private Mate mate;
    private Mate mate2;

    @BeforeEach
    void setUp() {
        Member member = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
        room = RoomFixture.정상_1(member);
        mate = MateFixture.정상_1(room, member);
        mate2 = MateFixture.정상_2(room, member);
    }

    @Nested
    class checkUpdatePermission {

        @Test
        @DisplayName("Mate가 Role의 할당자라면 성공한다.")
        void success_when_mate_is_assignee_of_role() {
            // given
            Role role = RoleFixture.정상_1(room, mate, List.of(mate, mate2));
            // when-then
            assertThatCode(() -> roleValidator.checkUpdatePermission(role, mate))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Mate가 Role의 할당자가 아니라면 예외를 던진다.")
        void failure_when_mate_is_not_assignee_of_role() {
            // given
            Role role = RoleFixture.정상_1(room, mate, List.of(mate2));
            // when-then
            assertThatCode(() -> roleValidator.checkUpdatePermission(role, mate))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    class checkRoleMaxLimit {

        @Test
        @DisplayName("Role의 개수가 MAX_ROLE_COUNT 미만이면 성공한다.")
        void success_when_role_count_is_under_max_limit() {
            // given
            given(roleRepositoryService.getRoleCountByRoomId(any(Long.class)))
                .willReturn(9);
            // when-then
            assertThatCode(() -> roleValidator.checkRoleMaxLimit(1L))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Role의 개수가 MAX_ROLE_COUNT이면 예외를 반환한다.")
        void failure_when_role_count_is_max_limit() {
            // given
            given(roleRepositoryService.getRoleCountByRoomId(any(Long.class)))
                .willReturn(10);
            // when-then
            assertThatThrownBy(() -> roleValidator.checkRoleMaxLimit(1L))
                .isInstanceOf(GeneralException.class);
        }

        @Test
        @DisplayName("Role의 개수가 MAX_ROLE_COUNT 초과면 예외를 반환한다.")
        void failure_when_role_count_is_over_max_limit() {
            // given
            given(roleRepositoryService.getRoleCountByRoomId(any(Long.class)))
                .willReturn(11);
            // when-then
            assertThatThrownBy(() -> roleValidator.checkRoleMaxLimit(1L))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    class checkMateIdListInSameRoom {

        // 다른 Room에서 존재하는 mate3
        private Mate mate3;

        @BeforeEach
        void setUp() {
            Member member2 = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
            Room room2 = RoomFixture.정상_2(member2);
            mate3 = MateFixture.정상_3(room2, member2);
        }

        @Test
        @DisplayName("모든 mate가 같은 방에 있으면 성공한다.")
        void success_when_all_mates_are_in_same_room() {
            // given
            List<Long> mateIdList = List.of(1L, 2L);
            given(mateRepository.findAllById(mateIdList))
                .willReturn(List.of(mate, mate2));
            // when-then
            assertThatCode(() -> roleValidator.checkMateIdListInSameRoom(mateIdList, 1L))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("모든 mate가 같은 방에 있지 않으면 예외를 반환한다.")
        void success_when_all_mates_are_not_in_same_room() {
            // given
            List<Long> mateIdList = List.of(1L, 2L, 3L);
            given(mateRepository.findAllById(mateIdList))
                .willReturn(List.of(mate, mate2, mate3));
            // when-then
            assertThatThrownBy(() -> roleValidator.checkMateIdListInSameRoom(mateIdList, 1L))
                .isInstanceOf(GeneralException.class);
        }
    }

}
