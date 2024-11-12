package com.cozymate.cozymate_server.domain.room.dto.response;

import com.cozymate.cozymate_server.domain.memberstat.enums.DifferenceStatus;
import java.util.List;
import lombok.Builder;

@Builder
public record RoomMemberStatDetailListDTO(
    List<RoomMemberStatDetailDTO> memberList,
    String color
) {

}
