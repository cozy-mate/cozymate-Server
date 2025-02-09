package com.cozymate.cozymate_server.domain.roomhashtag.service;

import com.cozymate.cozymate_server.domain.hashtag.Hashtag;
import com.cozymate.cozymate_server.domain.hashtag.converter.HashtagConverter;
import com.cozymate.cozymate_server.domain.hashtag.repository.HashtagRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roomhashtag.RoomHashtag;
import com.cozymate.cozymate_server.domain.roomhashtag.converter.RoomHashtagConverter;
import com.cozymate.cozymate_server.domain.roomhashtag.repository.RoomHashtagRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomHashtagCommandService {

    private final HashtagRepository hashtagRepository;
    private final RoomHashtagRepository roomHashtagRepository;

    public void createRoomHashtag(Room room, List<String> hashtags) {
        validateHashtags(hashtags);

        for (String tag : hashtags) {
            Hashtag hashtag = hashtagRepository.findByHashtag(tag)
                .orElseGet( ()-> hashtagRepository.save(HashtagConverter.toHashtag(tag)));

            RoomHashtag roomHashtag = RoomHashtagConverter.toRoomHashtag(room, hashtag);
            roomHashtagRepository.save(roomHashtag);
        }
    }

    // 입력한 해시태그 중복 검사
    private void validateHashtags(List<String> hashtags) {
        Set<String> uniqueHashtags = new HashSet<>(hashtags);
        if (uniqueHashtags.size() != hashtags.size()) {
            throw new GeneralException(ErrorStatus._DUPLICATE_HASHTAGS);
        }
    }

    public void deleteRoomHashtags(Room room) {
        roomHashtagRepository.deleteAllByRoomId(room.getId());
    }

    public void updateRoomHashtags(Room room, List<String> hashtags) {
        createRoomHashtag(room, hashtags);
    }
}
