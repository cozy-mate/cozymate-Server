package com.cozymate.cozymate_server.domain.message.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MessageListResponseDTO(
    Long memberId,
    List<MessageContentResponseDTO> content
) {

}