package com.cozymate.cozymate_server.domain.university.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record UniversityRequestDTO(
        String name,
        String mailPattern,
        List<String>dormitoryNames,
        List<String> departments

) {
}
