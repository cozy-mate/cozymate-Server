package com.cozymate.cozymate_server.domain.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedResponseDTO {

    private String name;
    private String description;

}
