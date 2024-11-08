package com.cozymate.cozymate_server.domain.university.dto.response;

import lombok.Builder;

@Builder
public record UniversityBasicInfoDTO(
        Long id,
        String name) {
}
