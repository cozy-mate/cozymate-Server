package com.cozymate.cozymate_server.domain.university.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record UniversityListResponseDTO(
        List<UniversityBasicInfoResponseDTO> universityList
) {
}
