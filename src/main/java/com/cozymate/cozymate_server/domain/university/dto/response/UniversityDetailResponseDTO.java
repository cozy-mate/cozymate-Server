package com.cozymate.cozymate_server.domain.university.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record UniversityDetailResponseDTO(
        Long id,
        String name,
        String mailPattern,
        List<String> mailPatterns,
        List<String> dormitoryNames,
        List<String> departments) {
}
