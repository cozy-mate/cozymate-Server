package com.cozymate.cozymate_server.domain.roomhashtag.service;

import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roomhashtag.repository.RoomHashtagRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomHashtagQueryService {

    private final RoomHashtagRepository roomHashtagRepository;

    public Map<Long, List<String>> getRoomHashtagsByRoomIds(List<Long> roomIds) {
        return roomHashtagRepository.findByRoomIds(roomIds)
            .stream()
            .collect(Collectors.groupingBy(
                roomHashtag -> roomHashtag.getRoom().getId(),
                Collectors.mapping(roomHashtag -> roomHashtag.getHashtag().getHashtag(), Collectors.toList())
            ));
    }

    public Map<Long, List<String>> getRoomHashtagsByRooms(List<Room> rooms) {
        if (rooms.isEmpty()) {
            return Map.of(); // 빈 리스트 방지
        }

        List<Long> roomIds = rooms.stream().map(Room::getId).toList();
        return getRoomHashtagsByRoomIds(roomIds);
    }


}
