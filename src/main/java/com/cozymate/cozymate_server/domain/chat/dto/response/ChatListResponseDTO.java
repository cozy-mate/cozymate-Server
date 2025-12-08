package com.cozymate.cozymate_server.domain.chat.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record ChatListResponseDTO(
    boolean hasNext,
    List<ChatResponseDTO> chatResponseDTOList
) {

}