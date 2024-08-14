package com.cozymate.cozymate_server.domain.room.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.CozymateResponse;
import com.cozymate.cozymate_server.domain.room.dto.InviteRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomJoinResponse;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;

public class RoomConverter {
    public static Room toEntity(RoomCreateRequest request, String inviteCode){
        return Room.builder()
            .name(request.getName())
            .profileImage(request.getProfileImage())
            .maxMateNum(request.getMaxMateNum())
            .inviteCode(inviteCode)
            .status(RoomStatus.WAITING)
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

    public static InviteRequest toInviteRequest(Room room, Mate mate){
        return InviteRequest.builder()
            .roomId(room.getId())
            .managerNickname(mate.getMember().getNickname())
            .roomName(room.getName())
            .build();
    }

}
