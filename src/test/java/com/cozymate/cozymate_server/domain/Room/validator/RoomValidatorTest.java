package com.cozymate.cozymate_server.domain.Room.validator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.when;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepositoryService;
import com.cozymate.cozymate_server.domain.room.validator.RoomValidator;
import com.cozymate.cozymate_server.fixture.MateFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.RoomFixture;
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

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
public class RoomValidatorTest {

    @Mock
    private MateRepositoryService mateRepositoryService;
    @Mock
    private RoomRepositoryService roomRepositoryService;

    @InjectMocks
    private RoomValidator roomValidator;

    Member member;
    Room room;
    Mate mate;


    @BeforeEach
    void setUp() {
        member = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
        room = RoomFixture.정상_1(member);
        mate = MateFixture.정상_1(room,member);

    }

    @Nested
    class checkRoomAccess {
        @Test
        @DisplayName("방에 대한 접근권한이 존재한다면 성공한다")
        void success_when_member_is_joined_mate_status() {
            // given
            when(mateRepositoryService.getJoinedMateOrThrow(room.getId(), member.getId()))
                .thenReturn(mate);
            // when & then
            assertThatCode(() -> roomValidator.checkRoomAccess(room, member.getId()))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("방에 대한 접근권한이 없다면 실패한다")
        void fail_when_member_is_not_joined_mate_status() {
            // given
            when(mateRepositoryService.getJoinedMateOrThrow(room.getId(), member.getId()))
                .thenThrow(new GeneralException(ErrorStatus._NOT_ROOM_MATE));
            // when & then
            assertThatCode(() -> roomValidator.checkRoomAccess(room, member.getId()))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    class checkRoomManager {
        @Test
        @DisplayName("방의 매니저라면 성공한다")
        void success_when_member_is_manager_status() {
            // given
            Mate manager = MateFixture.정상_5(room, member);

            // when & then
            assertThatCode(() -> roomValidator.checkRoomManager(manager))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("방의 매니저가 아니라면 예외를 던진다")
        void fail_when_member_is_not_manager() {
            // when & then
            assertThatCode(() -> roomValidator.checkRoomManager(mate))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    class checkRoomFull {
        @Test
        @DisplayName("방에 자리가 있는 경우 성공한다")
        void success_when_room_is_not_full() {
            // when & then
            assertThatCode(() -> roomValidator.checkRoomFull(room))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("방이 정원 초과인 경우 예외를 던진다")
        void fail_when_room_is_full() {
            //given
            Member member2 = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
            Room fullRoom = RoomFixture.정상_3(member2);
            // when & then
            assertThatCode(() -> roomValidator.checkRoomFull(fullRoom))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    class checkAlreadyJoinedRoom {
        @Test
        @DisplayName("참여중인 방이 없으면 성공한다")
        void success_when_member_has_no_joined_room() {
            // given
            when(roomRepositoryService.getRoomParticipationExistsByMemberId(member.getId()))
                .thenReturn(false);

            // when & then
            assertThatCode(() -> roomValidator.checkAlreadyJoinedRoom(member.getId()))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("이미 참여중인 방이 있으면 예외를 던진다")
        void fail_when_member_has_joined_room() {
            //given
            when(roomRepositoryService.getRoomParticipationExistsByMemberId(member.getId()))
                .thenReturn(true);

            // when & then
            assertThatCode(() -> roomValidator.checkAlreadyJoinedRoom(member.getId()))
                .isInstanceOf(GeneralException.class);
        }
    }

    @Nested
    class checkEntryStatus {
        @Test
        @DisplayName("EntryStatus가 exited인 경우 성공한다")
        void success_when_status_is_exited() {
            // given
            Member member3 = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
            Mate exitedMate = MateFixture.정상_4(room, member3);

            // when & then
            assertThatCode(() -> roomValidator.checkEntryStatus(exitedMate))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("EntryStatus가 JOINED이면 예외를 던진다")
        void fail_when_status_is_joined() {
            // when & then
            assertThatCode(() -> roomValidator.checkEntryStatus(mate))
                .isInstanceOf(GeneralException.class);
        }

        @Test
        @DisplayName("EntryStatus가 PENDING이면 예외를 던진다")
        void fail_when_status_is_pending() {
            // given
            Member member4 = MemberFixture.정상_3(UniversityFixture.createTestUniversity());
            Mate pendingMate = MateFixture.정상_2(room, member4);

            // when & then
            assertThatCode(() -> roomValidator.checkEntryStatus(pendingMate))
                .isInstanceOf(GeneralException.class);
        }

        @Test
        @DisplayName("EntryStatus가 INVITED이면 예외를 던진다")
        void fail_when_status_is_invited() {
            // given
            Member member5 = MemberFixture.정상_4(UniversityFixture.createTestUniversity());
            Mate invitedMate = MateFixture.정상_3(room, member5);

            // when & then
            assertThatCode(() -> roomValidator.checkEntryStatus(invitedMate))
                .isInstanceOf(GeneralException.class);
        }
    }
}
