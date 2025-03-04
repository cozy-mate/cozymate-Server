package com.cozymate.cozymate_server.domain.feed.converter;

import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.feed.dto.FeedResponseDTO;


public class FeedConverter {

    public static FeedResponseDTO toDto(Feed feed) {
        return FeedResponseDTO.builder()
            .name(feed.getName())
            .description(feed.getDescription())
            .build();
    }

    public static Feed toEntity() {
        return Feed.builder()
            .name("")
            .description("")
            .build();
    }

}
