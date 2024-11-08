package com.cozymate.cozymate_server.domain.role.dto.response;

import lombok.Builder;

@Builder
public record RoleSimpleResponseDTO(
    Long roleId
) {
}
