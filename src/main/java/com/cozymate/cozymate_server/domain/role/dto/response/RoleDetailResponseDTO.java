package com.cozymate.cozymate_server.domain.role.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record RoleDetailResponseDTO(
    Long roleId,
    List<String>mateNameList,
    String content,
    List<String> repeatDayList,
    boolean isAllDays
) {

}
