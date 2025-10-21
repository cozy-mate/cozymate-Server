package com.cozymate.cozymate_server.domain.role.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.converter.RoleConverter;
import com.cozymate.cozymate_server.domain.role.dto.MateIdNameDTO;
import com.cozymate.cozymate_server.domain.role.dto.request.CreateRoleRequestDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleIdResponseDTO;
import com.cozymate.cozymate_server.domain.role.enums.DayListBitmask;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepositoryService;
import com.cozymate.cozymate_server.domain.role.validator.RoleValidator;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.todo.service.TodoCommandService;
import com.cozymate.cozymate_server.fixture.MateFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.RoleFixture;
import com.cozymate.cozymate_server.fixture.RoomFixture;
import com.cozymate.cozymate_server.fixture.UniversityFixture;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class RoleCommandServiceTest {

    @Mock
    private RoleRepositoryService roleRepositoryService;
    @Mock
    private MateRepository mateRepository;
    @Mock
    private TodoCommandService todoCommandService;
    @Mock
    private RoleValidator roleValidator;
    @Mock
    private Clock clock;

    @InjectMocks
    private RoleCommandService roleCommandService;

    private Member member;
    private Room room;
    private Mate mate;
    private Role role;

    @BeforeEach
    void setUp() {
        member = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
        room = RoomFixture.정상_1(member);
        mate = MateFixture.정상_1(room, member);
        given(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(
            any(Long.class), any(Long.class), any(EntryStatus.class)))
            .willReturn(Optional.of(mate));
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class createRole {

        Member memberOfMate;
        Long roomId;
        private CreateRoleRequestDTO requestDTO;

        @BeforeEach
        void setUp() {
            Member member2 = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
            Mate mate2 = MateFixture.정상_2(room, member2);
            memberOfMate = mate.getMember();
            roomId = room.getId();;

            role = RoleFixture.정상_1(room, mate, List.of(mate, mate2));
            requestDTO = new CreateRoleRequestDTO(
                List.of(
                    new MateIdNameDTO(
                        mate.getId(),
                        mate.getMember().getNickname()
                    ),
                    new MateIdNameDTO(
                        mate2.getId(),
                        mate2.getMember().getNickname()
                    )
                ),
                role.getContent(),
                RoleConverter.convertBitmaskToDayList(role.getRepeatDays()).stream()
                    .map(DayListBitmask::name)
                    .toList()
            );
        }

        @Test
        @DisplayName("검증을 모두 통과한 Role은 생성된다.")
        void success_create_role() {
            //given
            willDoNothing().given(roleValidator).checkRoleMaxLimit(any(Long.class));
            willDoNothing().given(roleValidator).checkMateIdListInSameRoom(any(), any(Long.class));
            given(roleRepositoryService.createRole(any(Role.class))).willReturn(role);

            //when
            RoleIdResponseDTO response = roleCommandService.createRole(
                mate.getMember(), room.getId(), requestDTO);
            //then
            assertThat(response).isNotNull();
            assertThat(response.roleId()).isPositive();
        }

        @Test
        @DisplayName("최대 개수 검증에 걸리면 예외를 반환한다.")
        void failure_max_limit_validation() {
            //given
            willThrow(new GeneralException(ErrorStatus._ROLE_MAX_LIMIT))
                .given(roleValidator).checkRoleMaxLimit(any(Long.class));
            willDoNothing().given(roleValidator).checkMateIdListInSameRoom(any(), any(Long.class));
            given(roleRepositoryService.createRole(any(Role.class))).willReturn(role);

            //when - then
            assertThatThrownBy(
                () -> roleCommandService.createRole(memberOfMate, roomId, requestDTO))
                .isInstanceOf(GeneralException.class);
        }

        @Test
        @DisplayName("mate가 모두 존재하지 않으면 예외를 반환한다.")
        void failure_some_mate_is_not_exist_validation() {
            //given
            willDoNothing()
                .given(roleValidator).checkRoleMaxLimit(any(Long.class));
            willThrow(new GeneralException(ErrorStatus._MATE_NOT_FOUND))
                .given(roleValidator).checkMateIdListInSameRoom(any(), any(Long.class));
            given(roleRepositoryService.createRole(any(Role.class))).willReturn(role);

            //when - then
            assertThatThrownBy(
                () -> roleCommandService.createRole(memberOfMate, roomId, requestDTO))
                .isInstanceOf(GeneralException.class);
        }

        @Test
        @DisplayName("mate가 모두 같은 방이 아니면면 예외를 반환한다.")
        void failure_mate_is_not_in_same_room_validation() {
            //given
            willDoNothing()
                .given(roleValidator).checkRoleMaxLimit(any(Long.class));
            willThrow(new GeneralException(ErrorStatus._MATE_NOT_IN_SAME_ROOM))
                .given(roleValidator).checkMateIdListInSameRoom(any(), any(Long.class));
            given(roleRepositoryService.createRole(any(Role.class))).willReturn(role);

            //when - then
            assertThatThrownBy(
                () -> roleCommandService.createRole(memberOfMate, roomId, requestDTO))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class deleteRole {

        private Long roomId;
        private Long roleId;

        @BeforeEach
        void setUp() {
            role = RoleFixture.정상_1(room, mate, List.of(mate));
            roleId = role.getId();
            roomId = room.getId();
            given(roleRepositoryService.getRoleOrThrow(any(Long.class)))
                .willReturn(role);
            willDoNothing()
                .given(roleRepositoryService).deleteRole(any(Role.class));
        }

        @Test
        @DisplayName("Role 수정 권한이 있으면 Role을 삭제한다.")
        void success_delete_role() {
            //given
            willDoNothing()
                .given(roleValidator).checkUpdatePermission(any(Role.class), any(Mate.class));

            // When - Then
            assertThatCode(() -> roleCommandService.deleteRole(member, roomId, role.getId()))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Role 수정 권한이 없으면 예외를 반환한다.")
        void failure_delete_role_when_no_permission() {
            //given
            willThrow(new GeneralException(ErrorStatus._ROLE_NOT_VALID))
                .given(roleValidator).checkUpdatePermission(any(Role.class), any(Mate.class));

            // When - Then
            assertThatThrownBy(
                () -> roleCommandService.deleteRole(member, roomId, roleId))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class removeMateFromAssignedList {

        private Role role3;

        @BeforeEach
        void setUp() {
            Mate mate2 = MateFixture.정상_2(room, member);
            Mate mate3 = MateFixture.정상_3(room, member);
            role = RoleFixture.정상_1(room, mate, List.of(mate, mate2, mate3));
            Role role2 = RoleFixture.정상_2(room, mate, List.of(mate2, mate3));
            role3 = RoleFixture.정상_3(room, mate, List.of(mate));
            given(roleRepositoryService.getRoleListByRoomId(any(Long.class)))
                .willReturn(List.of(role, role2, role3));
        }

        @Test
        @DisplayName("방에 존재하는 Role의 할당자에서 Mate 제거에 성공한다")
        void success_remove_mate_from_assigned_list() {
            //given
            willDoNothing().given(roleRepositoryService).deleteRole(any(Role.class));

            //when
            roleCommandService.removeMateFromAssignedList(mate, room.getId());

            //then
            verify(roleRepositoryService, times(1)).deleteRole(role3);
        }
    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class updateRole {

        private CreateRoleRequestDTO requestDTO;
        private Long roomId;
        private Long roleId;

        @BeforeEach
        void setUp() {
            role = RoleFixture.정상_1(room, mate, List.of(mate));
            given(roleRepositoryService.getRoleOrThrow(any(Long.class)))
                .willReturn(role);
            given(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(
                any(Long.class), any(Long.class), any(EntryStatus.class)))
                .willReturn(Optional.of(mate));

            requestDTO = new CreateRoleRequestDTO(
                List.of(
                    new MateIdNameDTO(
                        mate.getId(),
                        mate.getMember().getNickname()
                    )
                ),
                role.getContent(),
                RoleConverter.convertBitmaskToDayList(role.getRepeatDays()).stream()
                    .map(DayListBitmask::name)
                    .toList()
            );

            roomId = room.getId();
            roleId = role.getId();
        }

        @Test
        @DisplayName("Role 수정 권한이 있으면 Role을 수정한다.")
        void success_update_role() {
            //given
            willDoNothing()
                .given(roleValidator).checkUpdatePermission(any(Role.class), any(Mate.class));

            // When - Then
            assertThatCode(() -> roleCommandService.updateRole(member, roomId, roleId, requestDTO))
                .doesNotThrowAnyException();

        }

        @Test
        @DisplayName("Role 수정 권한이 없으면 예외를 반환한다.")
        void failure_update_role_when_no_permission() {
            //given
            willThrow(new GeneralException(ErrorStatus._ROLE_NOT_VALID))
                .given(roleValidator).checkUpdatePermission(any(Role.class), any(Mate.class));

            // When - Then
            assertThatThrownBy(
                () -> roleCommandService.updateRole(member, roomId, roleId, requestDTO))
                .isInstanceOf(GeneralException.class);
        }

    }


}
