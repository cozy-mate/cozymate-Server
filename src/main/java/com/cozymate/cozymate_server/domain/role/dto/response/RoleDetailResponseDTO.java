package com.cozymate.cozymate_server.domain.role.dto.response;

import com.cozymate.cozymate_server.domain.role.dto.MateIdNameDTO;
import java.util.List;
import lombok.Builder;

@Builder
public record RoleDetailResponseDTO(
    Long roleId,
    List<MateIdNameDTO> mateList,
    String content,
    List<String> repeatDayList,
    boolean isAllDays
) {

}
