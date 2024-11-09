package com.cozymate.cozymate_server.domain.role.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record RoleListResponseDTO(
    List<RoleDetailResponseDTO> roleList
) {
}
