package com.cozymate.cozymate_server.domain.feed.dto;

import lombok.Builder;

@Builder
public record FeedResponseDTO(

    String name,
    String description
) {

}
