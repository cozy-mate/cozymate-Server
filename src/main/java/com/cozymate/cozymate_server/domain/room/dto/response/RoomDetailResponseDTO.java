package com.cozymate.cozymate_server.domain.room.dto.response;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatDifferenceListResponseDTO;
import java.util.List;

public record RoomDetailResponseDTO(
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

}
