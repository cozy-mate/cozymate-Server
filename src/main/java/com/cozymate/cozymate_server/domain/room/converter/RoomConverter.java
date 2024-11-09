package com.cozymate.cozymate_server.domain.room.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.CozymateInfoResponse;
import com.cozymate.cozymate_server.domain.room.dto.CozymateResponse;
import com.cozymate.cozymate_server.domain.room.dto.InviteRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomRequestDto.PrivateRoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomRequestDto.PublicRoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomResponseDto.RoomExistResponse;
import com.cozymate.cozymate_server.domain.room.dto.RoomResponseDto.RoomJoinResponse;
import com.cozymate.cozymate_server.domain.room.dto.RoomResponseDto.RoomListResponse;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import java.util.List;

public class RoomConverter {

    public static Room toPrivateRoom(PrivateRoomCreateRequest request, String inviteCode) {
        return Room.builder()
            .name(request.getName())
            .profileImage(request.getProfileImage())
            .maxMateNum(request.getMaxMateNum())
            .inviteCode(inviteCode)
            .status(RoomStatus.ENABLE)
            .roomType(RoomType.PRIVATE)
            .numOfArrival(1)
            .build();
    }

    public static Room toPublicRoom(PublicRoomCreateRequest request, String inviteCode) {
        return Room.builder()
            .name(request.getName())
            .profileImage(request.getProfileImage())
            .maxMateNum(request.getMaxMateNum())
            .inviteCode(inviteCode)
            .status(RoomStatus.WAITING)
            .roomType(RoomType.PUBLIC)
            .numOfArrival(1)
            .build();
    }


    public static RoomJoinResponse toRoomJoinResponse(Room room, Member manager) {
        return RoomJoinResponse.builder()
            .roomId(room.getId())
            .name(room.getName())
            .managerName(manager.getNickname())
            .maxMateNum(room.getMaxMateNum())
            .build();
    }

    public static CozymateResponse toCozymateResponse(Member member) {
        return CozymateResponse.builder()
            .memberId(member.getId())
            .nickname(member.getNickname())
            .build();
    }

    public static CozymateInfoResponse toCozymateInfoResponse(Mate mate, Integer mateEquality) {
        return CozymateInfoResponse.builder()
            .memberId(mate.getMember().getId())
            .mateId(mate.getId())
            .nickname(mate.getMember().getNickname())
            .persona(mate.getMember().getPersona())
            .mateEquality(mateEquality)
            .build();
    }

    public static InviteRequest toInviteRequest(Room room, Mate mate) {
        return InviteRequest.builder()
            .roomId(room.getId())
            .managerNickname(mate.getMember().getNickname())
            .roomName(room.getName())
            .build();
    }

    public static RoomExistResponse toRoomExistResponse(Room room) {
        if (room != null) {
            return RoomExistResponse.builder()
                .roomId(room.getId())
                .build();
        } else {
            return RoomExistResponse.builder()
                .roomId(0L) // 방이 없는 경우 roomId를 0으로 설정
                .build();
        }
    }

    // 이런식으로 엔티티를 DTO로 만들 땐 From Entity로 이름을 붙여주시고요, WithParams로 파라미터로 만드는 Converter도 만들어주면 좋습니다.
    public static RoomDetailResponseDTO toRoomDetailResponseDTOFromEntity(Room room) {
        return toRoomDetailResponseDTOWithParams(
            room.getId(),
            room.getName(),
            room.getInviteCode(),
            room.getProfileImage(),
            room.getMaxMateNum(),
            room.getNumOfArrival(),
            room.getRoomType().toString(),
            List.of()
        );
    }

    public static RoomDetailResponseDTO toRoomDetailResponseDTOWithParams(Long roomId, String name,
        String inviteCode, Integer persona, Integer maxMateNum, Integer arrivalMateNum,
        String roomType, List<String> hashtags) {
        return RoomDetailResponseDTO.builder()
            .roomId(roomId)
            .name(name)
            .inviteCode(inviteCode)
            .persona(persona)
            .maxMateNum(maxMateNum)
            .arrivalMateNum(arrivalMateNum)
            .roomType(roomType)
            .hashtags(hashtags)
            .build();
    }

    public static RoomListResponse toRoomListResponse(Room room, Integer roomEquality, List<String> hashtags) {
        return RoomListResponse.builder()
            .roomId(room.getId())
            .name(room.getName())
            .roomEquality(roomEquality)
            .hashtags(hashtags)
            .numOfArrival(room.getNumOfArrival())
            .build();
    }
}
