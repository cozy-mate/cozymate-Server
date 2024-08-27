package com.cozymate.cozymate_server.domain.feed.converter;

import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.feed.dto.FeedRequestDTO;
import com.cozymate.cozymate_server.domain.feed.dto.FeedResponseDTO;
import com.cozymate.cozymate_server.domain.room.Room;


public class FeedConverter {

    public static Feed toEntity(Room room, FeedRequestDTO requestDTO) {
        return Feed.builder()
            .room(room)
            .name(requestDTO.getName())
            .description(requestDTO.getDescription())
            .build();
    }

    public static FeedResponseDTO toDto(Feed feed) {
        return FeedResponseDTO.builder()
            .name(feed.getName())
            .description(feed.getDescription())
            .build();
    }

    public static Feed toEntity(Room room) {
        return Feed.builder()
            .room(room)
            .name("")
            .description("")
            .build();
    }

}
