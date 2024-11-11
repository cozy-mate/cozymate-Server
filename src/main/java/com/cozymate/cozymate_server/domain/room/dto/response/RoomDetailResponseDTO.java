package com.cozymate.cozymate_server.domain.room.dto.response;

import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatDifferenceResponseDTO;
import java.util.List;

public record RoomDetailResponseDTO(
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

}
