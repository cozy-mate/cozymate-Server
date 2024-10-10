package com.cozymate.cozymate_server.domain.roomhashtag.service;

import com.cozymate.cozymate_server.domain.hashtag.Hashtag;
import com.cozymate.cozymate_server.domain.hashtag.repository.HashtagRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roomhashtag.RoomHashtag;
import com.cozymate.cozymate_server.domain.roomhashtag.converter.RoomHashtagConverter;
import com.cozymate.cozymate_server.domain.roomhashtag.repository.RoomHashtagRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomHashtagCommandService {

    private final HashtagRepository hashtagRepository;
    private final RoomHashtagRepository roomHashtagRepository;

    public void createRoomHashtag(Room room, List<String> hashtags) {
        for (String tag : hashtags) {
            Hashtag hashtag = hashtagRepository.findByHashtag(tag)
                .orElseGet( ()-> hashtagRepository.save(new Hashtag(tag)));

            RoomHashtag roomHashtag = RoomHashtagConverter.toRoomHashtag(room, hashtag);
            roomHashtagRepository.save(roomHashtag);
        }

    }

}
