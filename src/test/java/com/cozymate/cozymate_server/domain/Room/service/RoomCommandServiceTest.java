package com.cozymate.cozymate_server.domain.Room.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import com.cozymate.cozymate_server.domain.feed.repository.FeedRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepositoryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.request.PrivateRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.PublicRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepositoryService;
import com.cozymate.cozymate_server.domain.room.service.RoomCommandService;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.domain.room.validator.RoomValidator;
import com.cozymate.cozymate_server.domain.roomhashtag.service.RoomHashtagCommandService;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.fixture.MateFixture;
import com.cozymate.cozymate_server.fixture.MemberFixture;
import com.cozymate.cozymate_server.fixture.MemberStatFixture;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
class RoomCommandServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private MateRepository mateRepository;
    @Mock
    private RoomQueryService roomQueryService;
    @Mock
    private RoomLogCommandService roomLogCommandService;
    @Mock
    private RoomHashtagCommandService roomHashtagCommandService;
    @Mock
    private ApplicationEventPublisher eventPublisher; // 추가된 Mock
    @Mock
    private FeedRepository feedRepository;
    @Mock
    private RoomValidator roomValidator;
    @Mock
    private RoomRepositoryService roomRepositoryService;
    @Mock
    private MemberStatRepositoryService memberStatRepositoryService;
    @Mock
    private MemberRepositoryService memberRepositoryService;
    @Mock
    private MateRepositoryService mateRepositoryService;
    @InjectMocks
    private RoomCommandService roomCommandService;

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class createRoom {

        private Room room;
        private Member member1;
        private MemberStat memberStat;

        @BeforeEach
        void setUp() {

            memberStat = MemberStatFixture.정상_1(member1);
            member1 = MemberFixture.정상_1(UniversityFixture.createTestUniversity());

        }

        @Test
        @DisplayName("참여한 방이 없는 경우 비공개 방 생성 성공")
        void success_create_private_room_when_no_joined_room() {
            // Given
            room = RoomFixture.정상_1(member1);
            given(roomRepositoryService.createRoom(any(Room.class))).willReturn(room);

            PrivateRoomCreateRequestDTO request = RoomFixture.정상_1_생성_요청_DTO();

            // 이미 참여한 방이 없을 때
            willDoNothing()
                .given(roomValidator).checkAlreadyJoinedRoom(any(Long.class));

            given(roomQueryService.getRoomById(eq(room.getId()), eq(member1.getId())))
                .willReturn(RoomFixture.정상_1_응답_DTO(room, member1));

            // When
            RoomDetailResponseDTO responseDTO = roomCommandService.createPrivateRoom(request, member1);

            // Then
            assertThat(responseDTO.roomId()).isEqualTo(room.getId());
            then(roomRepositoryService).should(times(1)).createRoom(any(Room.class));
            then(mateRepository).should(times(1)).save(any(Mate.class));

        }

        @Test
        @DisplayName("참여한 방이 없는 경우 공개 방 생성 성공")
        void success_create_public_room_when_no_joined_room() {
            // Given
            room = RoomFixture.정상_2(member1);
            given(roomRepositoryService.createRoom(any(Room.class))).willReturn(room);

            PublicRoomCreateRequestDTO request = RoomFixture.정상_2_생성_요청_DTO();

            willDoNothing()
                .given(roomValidator).checkAlreadyJoinedRoom(any(Long.class));

            given(memberStatRepositoryService.getMemberStatOrThrow(any(Long.class)))
                .willReturn(memberStat);

            given(roomQueryService.getRoomById(eq(room.getId()), eq(member1.getId())))
                .willReturn(RoomFixture.정상_1_응답_DTO(room, member1));

            // When
            RoomDetailResponseDTO responseDTO = roomCommandService.createPublicRoom(request, member1);

            // Then
            assertThat(responseDTO.roomId()).isEqualTo(room.getId());
            then(roomRepositoryService).should(times(1)).createRoom(any(Room.class));
            then(mateRepository).should(times(1)).save(any(Mate.class));
            then(roomHashtagCommandService).should(times(1))
                .createRoomHashtag(eq(room), eq(request.hashtagList()));
        }

        @Test
        @DisplayName("이미 참여한 방이 있는 경우 비공개 방 생성 실패")
        void fail_create_private_room_when_joined_room() {
            // Given
            PrivateRoomCreateRequestDTO request = RoomFixture.정상_1_생성_요청_DTO();

            willThrow(new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS))
                .given(roomValidator).checkAlreadyJoinedRoom(member1.getId());

            // When & Then
            assertThatThrownBy(
                () -> roomCommandService.createPrivateRoom(request, member1))
                .isInstanceOf(GeneralException.class);
        }

        @Test
        @DisplayName("이미 참여한 방이 있는 경우 공개 방 생성 실패")
        void fail_create_public_room_when_joined_room() {
            // Given
            PublicRoomCreateRequestDTO request = RoomFixture.정상_2_생성_요청_DTO();

            willThrow(new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS))
                .given(roomValidator).checkAlreadyJoinedRoom(member1.getId());

            // When & Then
            assertThatThrownBy(
                () -> roomCommandService.createPublicRoom(request, member1))
                .isInstanceOf(GeneralException.class);
        }

    }

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class inviteRoom {

        private Room room;
        private Member inviter;
        private Member invitee;

        @BeforeEach
        void setUp() {
            inviter = MemberFixture.정상_1(UniversityFixture.createTestUniversity());
            invitee = MemberFixture.정상_2(UniversityFixture.createTestUniversity());
            room = RoomFixture.정상_2(inviter);

            given(roomQueryService.getExistRoom(inviter.getId()))
                .willReturn(new RoomIdResponseDTO(room.getId()));
            given(roomRepositoryService.getRoomOrThrow(room.getId()))
                .willReturn(room);
        }

        @Test
        @DisplayName("초대하는 사람이 방장일때 초대 성공")
        void success_when_inviter_is_room_manager() {
            // Given
            given(mateRepositoryService.getJoinedMateFetchMemberOrThrow(room.getId(), inviter.getId()))
                .willReturn(MateFixture.정상_5(room, inviter));

            // When
            roomCommandService.sendInvitation(invitee.getId(), inviter);

            // Then
            then(mateRepository).should(times(1)).save(any(Mate.class));
        }

        @Test
        @DisplayName("초대하는 사람이 방장이 아닌 경우 초대 실패")
        void fail_when_inviter_is_not_room_manager() {
            // Given
            Mate inviterMate = MateFixture.정상_1(room, inviter);
            given(mateRepositoryService.getJoinedMateFetchMemberOrThrow(room.getId(), inviter.getId()))
                .willReturn(inviterMate);

            willThrow(new GeneralException(ErrorStatus._NOT_ROOM_MANAGER))
                .given(roomValidator).checkRoomManager(inviterMate);

            assertThatThrownBy(
                () -> roomCommandService.sendInvitation(invitee.getId(), inviter))
                .isInstanceOf(GeneralException.class);
        }

    }

}