package com.cozymate.cozymate_server.domain.room.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record RoomMemberStatDetailListDTO(
    List<RoomMemberStatDetailDTO> memberList
) {

}
