package com.cozymate.cozymate_server.domain.role.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.role.Role;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleDetailResponseDTO;
import com.cozymate.cozymate_server.domain.role.repository.RoleRepositoryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.fixture.MateFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.RoleFixture;
import com.cozymate.cozymate_server.fixture.RoomFixture;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class RoleQueryServiceTest {

    @Mock
    private MateRepository mateRepository;
    @Mock
    private RoleRepositoryService roleRepositoryService;
    @Mock
    private RoleCommandService roleCommandService;

    @InjectMocks
    private RoleQueryService roleQueryService;

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class getRole {

        Member member;
        Room room;
        Mate mate;
        Role role;
        Role role2;

        @BeforeEach
        void setup() {
            // given
            member = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
            room = RoomFixture.정상_1(member);
            mate = MateFixture.정상_1(room, member);
            role = RoleFixture.정상_1(room, mate, List.of(mate));
            role2 = RoleFixture.정상_2(room, mate, List.of(mate));

            given(mateRepository.findAllByRoomIdAndEntryStatus(any(Long.class),
                any(EntryStatus.class)))
                .willReturn(List.of(mate));
            given(mateRepository.findByRoomIdAndMemberIdAndEntryStatus(
                any(Long.class), any(Long.class), any(EntryStatus.class)))
                .willReturn(Optional.of(mate));
            willDoNothing().given(roleCommandService).deleteRoleIfMateEmpty(any(Role.class));
        }

        @Test
        @DisplayName("특정 방의 role 목록을 조회한다.")
        void success() {
            // given
            given(roleRepositoryService.getRoleListByRoomId(any(Long.class)))
                .willReturn(List.of(role, role2));
            // when
            List<RoleDetailResponseDTO> roleDetailResponseDTOList = roleQueryService.getRole(
                member, room.getId());
            // then
            assertThat(roleDetailResponseDTOList).hasSize(2);

            assertThat(roleDetailResponseDTOList.get(0).roleId()).isEqualTo(role.getId());
            assertThat(roleDetailResponseDTOList.get(1).roleId()).isEqualTo(role2.getId());

            assertThat(roleDetailResponseDTOList.get(0).repeatDayList())
                .isEqualTo(List.of("월"));
            assertThat(roleDetailResponseDTOList.get(1).repeatDayList())
                .isEqualTo(List.of("월", "화", "수", "목", "금", "토", "일"));

            assertThat(roleDetailResponseDTOList.get(0).isAllDays()).isEqualTo(false);
            assertThat(roleDetailResponseDTOList.get(1).isAllDays()).isEqualTo(true);
        }

        @Test
        @DisplayName("특정 방의 비어있는 role 목록을 조회한다.")
        void success_when_empty() {
            // given
            given(roleRepositoryService.getRoleListByRoomId(any(Long.class)))
                .willReturn(List.of());
            // when
            List<RoleDetailResponseDTO> roleDetailResponseDTOList = roleQueryService.getRole(
                member, room.getId());
            // then
            assertThat(roleDetailResponseDTOList).hasSize(0);
        }
    }

}
