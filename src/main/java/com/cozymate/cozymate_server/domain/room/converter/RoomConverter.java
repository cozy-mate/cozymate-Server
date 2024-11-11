package com.cozymate.cozymate_server.domain.room.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatDifferenceResponseDTO;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.request.PrivateRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.PublicRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.MateDetailListReponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomListResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomSimpleResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import java.util.List;

public class RoomConverter {

    public static Room toPrivateRoom(PrivateRoomCreateRequestDTO request, String inviteCode) {
        return Room.builder()
            .name(request.name())
            .profileImage(request.persona())
            .maxMateNum(request.maxMateNum())
            .inviteCode(inviteCode)
            .status(RoomStatus.ENABLE)
            .roomType(RoomType.PRIVATE)
            .numOfArrival(1)
            .build();
    }

    public static Room toPublicRoom(PublicRoomCreateRequestDTO request, String inviteCode) {
        return Room.builder()
            .name(request.name())
            .profileImage(request.persona())
            .maxMateNum(request.maxMateNum())
            .inviteCode(inviteCode)
            .status(RoomStatus.WAITING)
            .roomType(RoomType.PUBLIC)
            .numOfArrival(1)
            .build();
    }

    public static MateDetailListReponseDTO toMateDetailListResponse(Mate mate, Integer mateEquality) {
        return new MateDetailListReponseDTO(
            mate.getMember().getId(),
            mate.getId(),
            mate.getMember().getNickname(),
            mate.getMember().getPersona(),
            mateEquality
        );
    }

    public static RoomSimpleResponseDTO toRoomExistResponse(Room room) {
        return new RoomSimpleResponseDTO(room != null ? room.getId() : 0L);
    }

    public static RoomDetailResponseDTO toRoomDetailResponseDTOWithParams(
        Long roomId,
        String name,
        String inviteCode,
        Integer persona,
        List<MateDetailListReponseDTO> mateDetailList,
        Long managerMemberId,
        String managerNickname,
        Boolean isRoomManager,
        Integer maxMateNum,
        Integer arrivalMateNum,
        String roomType,
        List<String> hashtagList,
        Integer equality,
        MemberStatDifferenceResponseDTO difference
    ) {
        return new RoomDetailResponseDTO(
            roomId,
            name,
            inviteCode,
            persona,
            mateDetailList,
            managerMemberId,
            managerNickname,
            isRoomManager,
            maxMateNum,
            arrivalMateNum,
            roomType,
            hashtagList,
            equality,
            difference
        );
    }

    public static RoomListResponseDTO toRoomListResponse(Room room, Integer roomEquality, List<String> hashtagList) {
        return new RoomListResponseDTO(
            room.getId(),
            room.getName(),
            roomEquality,
            hashtagList,
            room.getNumOfArrival()
        );
    }
}
