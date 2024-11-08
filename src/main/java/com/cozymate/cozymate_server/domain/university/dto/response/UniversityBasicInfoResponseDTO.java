package com.cozymate.cozymate_server.domain.university.dto.response;

import lombok.Builder;

@Builder
public record UniversityBasicInfoResponseDTO(
        Long id,
        String name) {
}
