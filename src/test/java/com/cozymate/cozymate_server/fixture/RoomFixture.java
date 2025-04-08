package com.cozymate.cozymate_server.fixture;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.request.PrivateRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.PublicRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class RoomFixture {

    // 정원 6명 비공개방
    public static Room 정상_1(Member member) {
        return Room.builder()
            .id(1L)
            .name("비공개방")
            .profileImage(3)
            .maxMateNum(6)
            .inviteCode("abcd1234")
            .status(RoomStatus.ENABLE)
            .roomType(RoomType.PRIVATE)
            .numOfArrival(1)
            .build();
    }

    // 정원 6명 공개방
    public static Room 정상_2(Member member) {
        return Room.builder()
            .id(2L)
            .name("공개방")
            .profileImage(3)
            .maxMateNum(6)
            .inviteCode("abcd4567")
            .status(RoomStatus.WAITING)
            .roomType(RoomType.PUBLIC)
            .numOfArrival(1)
            .build();
    }

    // 정원 6명 꽉찬 공개방
    public static Room 정상_3(Member member) {
        return Room.builder()
            .id(3L)
            .name("꽉찬방")
            .profileImage(3)
            .maxMateNum(6)
            .inviteCode("hihi1234")
            .status(RoomStatus.ENABLE)
            .roomType(RoomType.PUBLIC)
            .numOfArrival(6)
            .build();
    }

    // 비정상, 방 이름에 특수 문자
    public static Room 방_제목이_특수문자(Member member) {
        return Room.builder()
            .id(4L)
            .name("^-^")
            .profileImage(3)
            .maxMateNum(6)
            .inviteCode("abcd8910")
            .status(RoomStatus.WAITING)
            .roomType(RoomType.PUBLIC)
            .numOfArrival(1)
            .build();
    }

    // 비정상, 방 이름에 특수 문자
    public static Room 정원이_6명보다_많음(Member member) {
        return Room.builder()
            .id(5L)
            .name("^-^")
            .profileImage(3)
            .maxMateNum(8)
            .inviteCode("efgh1234")
            .status(RoomStatus.WAITING)
            .roomType(RoomType.PUBLIC)
            .numOfArrival(1)
            .build();
    }

    public static PrivateRoomCreateRequestDTO 정상_1_생성_요청_DTO() {
        return PrivateRoomCreateRequestDTO.builder()
            .name("비공개방")
            .persona(3)
            .maxMateNum(6)
            .build();
    }

    public static PublicRoomCreateRequestDTO 정상_2_생성_요청_DTO() {
        return PublicRoomCreateRequestDTO.builder()
            .name("공개방")
            .persona(3)
            .maxMateNum(6)
            .hashtagList(List.of("해시", "태그"))
            .build();
    }

    public static RoomDetailResponseDTO 정상_1_응답_DTO(Room room, Member member) {
        return new RoomDetailResponseDTO(
            room.getId(),
            room.getName(),
            room.getInviteCode(),
            room.getProfileImage(),
            List.of(),
            member.getId(),
            member.getNickname(),
            true,
            null,
            room.getMaxMateNum(),
            room.getNumOfArrival(),
            null,
            room.getRoomType().toString(),
            null,
            0,
            null
        );
    }

    public static RoomDetailResponseDTO 정상_2_응답_DTO(Room room, Member member) {
        return new RoomDetailResponseDTO(
            room.getId(),
            room.getName(),
            room.getInviteCode(),
            room.getProfileImage(),
            List.of(),
            member.getId(),
            member.getNickname(),
            true,
            null,
            room.getMaxMateNum(),
            room.getNumOfArrival(),
            null,
            room.getRoomType().toString(),
            List.of("해시", "태그"),
            0,
            null
        );
    }


}
