package com.cozymate.cozymate_server.domain.Room;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.cozymate.cozymate_server.domain.feed.repository.FeedRepository;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.member.enums.Role;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.request.PrivateRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.PublicRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.room.service.RoomCommandService;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.domain.roomhashtag.service.RoomHashtagCommandService;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogCommandService;
import com.cozymate.cozymate_server.fixture.MateFixture;
import com.cozymate.cozymate_server.fixture.RoomFixture;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
    @InjectMocks
    private RoomCommandService roomCommandService;

    @Nested
    @MockitoSettings(strictness = Strictness.LENIENT)
    class createRoom {

        private Room room;
        private Mate mate;
        private Member member1;
        private Member member2;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);

            // TODO: 추후 member Fixture로 대체
            //  테스트에 사용할 방장 Member 생성
            member1 = Member.builder()
                .id(1L)
                .socialType(SocialType.KAKAO)
                .role(Role.USER)
                .clientId("qwer:KAKAO")
                .nickname("하이")
                .gender(Gender.MALE)
                .birthDay(LocalDate.of(2000, 1, 1))
                .persona(1)
                .build();
            member2 = Member.builder()
                .id(2L)
                .socialType(SocialType.KAKAO)
                .role(Role.USER)
                .clientId("qwer:KAKAO")
                .nickname("바이")
                .gender(Gender.MALE)
                .birthDay(LocalDate.of(2000, 1, 1))
                .persona(1)
                .build();

            given(memberRepository.findById(member1.getId()))
                .willReturn(Optional.of(member1));
        }

        @Test
        @DisplayName("참여한 방이 없는 경우 비공개 방 생성 성공")
        void success_create_private_room_when_no_joined_room() {
            // Given
            // 1) 가정: 방 생성용 DTO (실제 코드에 맞춰 커스텀)
            PrivateRoomCreateRequestDTO request = RoomFixture.정상_1_생성_요청_DTO();

            // 2) 이미 참여한 방이 없을 때
            given(roomRepository.existsByMemberIdAndStatuses(
                eq(member1.getId()),
                eq(RoomStatus.ENABLE),
                eq(RoomStatus.WAITING),
                eq(EntryStatus.JOINED)
            )).willReturn(false);

            room = RoomFixture.정상_1(member1);
            given(roomRepository.save(any(Room.class)))
                .willReturn(room);

            // roomDetailResponseDTO 반환
            given(roomQueryService.getRoomById(eq(room.getId()), eq(member1.getId())))
                .willReturn(new RoomDetailResponseDTO(
                        room.getId(),
                        room.getName(),
                        room.getInviteCode(),
                        room.getProfileImage(),
                        List.of(),
                        member1.getId(),
                        member1.getNickname(),
                        true, // 방장인지 확인
                        null,
                        room.getMaxMateNum(),
                        room.getNumOfArrival(),
                        null,
                        room.getRoomType().toString(),
                        null,
                        0,
                        null
                    )
                );

            // When
            RoomDetailResponseDTO responseDTO = roomCommandService.createPrivateRoom(request,
                member1);

            // Then
            // 1) Room에 대한 검증
            assertEquals(room.getId(), responseDTO.roomId());
            assertEquals(room.getName(), responseDTO.name());
            assertEquals(room.getInviteCode(), responseDTO.inviteCode());
            assertEquals(room.getRoomType().name(), responseDTO.roomType());

            // 2) Mate가 자동으로 생기는지 검증
            ArgumentCaptor<Mate> mateCaptor = ArgumentCaptor.forClass(Mate.class);
            verify(mateRepository).save(mateCaptor.capture());
            mate = mateCaptor.getValue();

            // 3) 방장 Mate가 올바르게 생성되었는지 확인
            assertEquals(room.getId(), mate.getRoom().getId());
            assertEquals(member1.getId(), mate.getMember().getId());
            assertEquals(EntryStatus.JOINED, mate.getEntryStatus());
            assertTrue(mate.isRoomManager());

        }

        @Test
        @DisplayName("참여한 방이 없는 경우 공개 방 생성 성공")
        void success_create_public_room_when_no_joined_room() {
            // Given
            // 1) 공개 방 생성 요청 DTO
            PublicRoomCreateRequestDTO request = RoomFixture.정상_2_생성_요청_DTO();

            // 2) 이미 참여한 방이 없을 때
            given(roomRepository.existsByMemberIdAndStatuses(
                eq(member1.getId()),
                eq(RoomStatus.ENABLE),
                eq(RoomStatus.WAITING),
                eq(EntryStatus.JOINED)
            )).willReturn(false);

            // 3) RoomFixture로 공개방 엔티티 생성
            room = RoomFixture.정상_2(member1);
            given(roomRepository.save(any(Room.class)))
                .willReturn(room);

            // 4) roomDetailResponseDTO 반환
            given(roomQueryService.getRoomById(eq(room.getId()), eq(member1.getId())))
                .willReturn(new RoomDetailResponseDTO(
                    room.getId(),
                    room.getName(),
                    room.getInviteCode(),
                    room.getProfileImage(),
                    List.of(),
                    member1.getId(),
                    member1.getNickname(),
                    true,
                    null,
                    room.getMaxMateNum(),
                    room.getNumOfArrival(),
                    null,
                    room.getRoomType().toString(),
                    List.of("해시", "태그"),
                    0,
                    null
                ));

            // When
            RoomDetailResponseDTO responseDTO
                = roomCommandService.createPublicRoom(request, member1);

            // Then
            // 1) Room에 대한 검증
            assertEquals(room.getId(), responseDTO.roomId());
            assertEquals(room.getName(), responseDTO.name());
            assertEquals(room.getInviteCode(), responseDTO.inviteCode());
            assertEquals(room.getRoomType().name(), responseDTO.roomType());

            // 2) Mate가 자동으로 생기는지 검증
            ArgumentCaptor<Mate> mateCaptor = ArgumentCaptor.forClass(Mate.class);
            verify(mateRepository).save(mateCaptor.capture());
            mate = mateCaptor.getValue();

            // 3) 방장 Mate가 올바르게 생성되었는지 확인
            assertEquals(room.getId(), mate.getRoom().getId());
            assertEquals(member1.getId(), mate.getMember().getId());
            assertEquals(EntryStatus.JOINED, mate.getEntryStatus());
            assertTrue(mate.isRoomManager());

            // 4) 해시태그 생성 확인
            verify(roomHashtagCommandService).createRoomHashtag(any(Room.class), eq(List.of("해시", "태그")));
        }

        @Test
        @DisplayName("이미 참여한 방이 있는 경우 비공개 방 생성 실패")
        void fail_create_private_room_when_joined_room() {
            // Given
            PrivateRoomCreateRequestDTO request = RoomFixture.정상_1_생성_요청_DTO();

            // 이미 참여한 방이 있을 때
            given(roomRepository.existsByMemberIdAndStatuses(
                eq(member1.getId()),
                eq(RoomStatus.ENABLE),
                eq(RoomStatus.WAITING),
                eq(EntryStatus.JOINED)
            )).willReturn(true);

            // When & Then
            assertThrows(GeneralException.class,
                () -> roomCommandService.createPrivateRoom(request, member1)
            );
        }

        @Test
        @DisplayName("이미 참여한 방이 있는 경우 공개 방 생성 실패")
        void fail_create_public_room_when_joined_room() {
            // Given
            PublicRoomCreateRequestDTO request = RoomFixture.정상_2_생성_요청_DTO();

            // 이미 참여한 방이 있을 때
            given(roomRepository.existsByMemberIdAndStatuses(
                eq(member1.getId()),
                eq(RoomStatus.ENABLE),
                eq(RoomStatus.WAITING),
                eq(EntryStatus.JOINED)
            )).willReturn(true);

            // When & Then
            assertThrows(GeneralException.class,
                () -> roomCommandService.createPublicRoom(request, member1)
            );
        }

        @Test
        @DisplayName("방 초대 성공")
        void sendInvitation_Success() {
            // Given
            Member inviter = member1;
            Member invitee = member2;
            Room room = RoomFixture.정상_2(inviter);

            given(memberRepository.findById(invitee.getId())).willReturn(Optional.of(invitee));
            given(roomQueryService.getExistRoom(inviter.getId())).willReturn(new RoomIdResponseDTO(room.getId()));
            given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
            given(mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true))
                .willReturn(Optional.of(MateFixture.정상_5(room, inviter)));
            given(mateRepository.findByRoomIdAndMemberId(room.getId(), invitee.getId())).willReturn(Optional.empty());
            given(roomRepository.existsByMemberIdAndStatuses(invitee.getId(), RoomStatus.ENABLE, RoomStatus.WAITING, EntryStatus.JOINED))
                .willReturn(false);

            // When
            roomCommandService.sendInvitation(invitee.getId(), inviter);

            // Then
            verify(mateRepository).save(any(Mate.class));
        }

        @Test
        @DisplayName("방장이 아닌 경우 초대 실패")
        void sendInvitation_Failure_NotRoomManager() {
            // Given
            Member inviter = member1;
            Member invitee = member2;
            Room room = RoomFixture.정상_2(inviter);

            given(memberRepository.findById(invitee.getId())).willReturn(Optional.of(invitee));
            given(roomQueryService.getExistRoom(inviter.getId())).willReturn(new RoomIdResponseDTO(room.getId()));
            given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
            given(mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)).willReturn(Optional.empty());

            // When & Then
            assertThrows(GeneralException.class, () -> roomCommandService.sendInvitation(invitee.getId(), inviter));
        }

    }

}