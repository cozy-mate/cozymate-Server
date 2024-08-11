package com.cozymate.cozymate_server.domain.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class FeedResponseDTO {

    private String name;
    private String description;

}
