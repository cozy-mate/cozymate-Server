package com.cozymate.cozymate_server.domain.room.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDifferenceListResponseDTO;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.request.PrivateRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.PublicRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.InvitedRoomResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.MateDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomExistResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomSearchResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.university.University;
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

    public static Room toPublicRoom(PublicRoomCreateRequestDTO request, String inviteCode, Gender gender, University university) {
        return Room.builder()
            .name(request.name())
            .profileImage(request.persona())
            .maxMateNum(request.maxMateNum())
            .inviteCode(inviteCode)
            .status(RoomStatus.WAITING)
            .roomType(RoomType.PUBLIC)
            .numOfArrival(1)
            .gender(gender)
            .university(university)
            .description(request.description())
            .build();
    }

    public static RoomSearchResponseDTO toRoomSearchResponseDTO(Room room, Integer equality) {
        return new RoomSearchResponseDTO(
            room.getId(),
            room.getName(),
            room.getNumOfArrival(),
            equality
        );
    }

    public static MateDetailResponseDTO toMateDetailListResponse(Mate mate, Integer mateEquality) {
        return new MateDetailResponseDTO(
            mate.getMember().getId(),
            mate.getId(),
            mate.getMember().getNickname(),
            mate.getMember().getPersona(),
            mateEquality
        );
    }

    public static RoomIdResponseDTO toRoomExistResponse(Room room) {
        return new RoomIdResponseDTO(room != null ? room.getId() : 0L);
    }

    public static RoomExistResponseDTO toRoomExistResponse(Room room, boolean isRoomManager) {
        if (room == null) {
            return new RoomExistResponseDTO(0L, false);
        }
        return new RoomExistResponseDTO(room.getId(), isRoomManager);
    }

    public static InvitedRoomResponseDTO toInvitedRoomResponseDTO(Integer invitedCount, List<RoomDetailResponseDTO> rooms) {
        return new InvitedRoomResponseDTO(invitedCount, rooms);
    }

    public static RoomDetailResponseDTO toRoomDetailResponseDTOWithParams(
        Long roomId,
        String name,
        String inviteCode,
        Integer persona,
        List<MateDetailResponseDTO> mateDetailList,
        Long managerMemberId,
        String managerNickname,
        Boolean isRoomManager,
        Long favoriteId,
        Integer maxMateNum,
        Integer arrivalMateNum,
        String dormitoryName,
        String roomType,
        String description,
        Integer equality,
        MemberStatDifferenceListResponseDTO difference
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
            favoriteId,
            maxMateNum,
            arrivalMateNum,
            dormitoryName,
            roomType,
            description,
            equality,
            difference
        );
    }
}
