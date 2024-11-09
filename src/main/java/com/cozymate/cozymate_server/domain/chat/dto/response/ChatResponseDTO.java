package com.cozymate.cozymate_server.domain.chat.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record ChatResponseDTO(
    Long memberId,
    List<ChatContentResponseDTO> content
) {

}