package com.cozymate.cozymate_server.domain.room.dto.response;

import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import java.util.Map;
import lombok.Builder;

@Builder
public record RoomMemberStatDetailDTO(
    MemberDetailResponseDTO memberDetail,
    Map<String, Object> memberStat
) {

}
