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
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;

public class RoomConverter {
    public static Room toPrivateRoom(PrivateRoomCreateRequest request, String inviteCode){
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

    public static Room toPublicRoom(PublicRoomCreateRequest request, String inviteCode){
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


    public static RoomJoinResponse toRoomJoinResponse(Room room, Member manager){
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

    public static CozymateInfoResponse toCozyMateInfoResponse(Mate mate) {
        return CozymateInfoResponse.builder()
            .memberId(mate.getMember().getId())
            .mateId(mate.getId())
            .nickname(mate.getMember().getNickname())
            .persona(mate.getMember().getPersona())
            .build();
    }

    public static InviteRequest toInviteRequest(Room room, Mate mate){
        return InviteRequest.builder()
            .roomId(room.getId())
            .managerNickname(mate.getMember().getNickname())
            .roomName(room.getName())
            .build();
    }

    public static RoomExistResponse toRoomExistResponse(Room room){
        if (room!=null) {
            return RoomExistResponse.builder()
                .roomId(room.getId())
                .build();
        } else {
            return RoomExistResponse.builder()
                .roomId(0L) // 방이 없는 경우 roomId를 0으로 설정
                .build();
        }
    }

}
