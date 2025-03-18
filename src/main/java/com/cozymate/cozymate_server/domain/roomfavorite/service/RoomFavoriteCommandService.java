package com.cozymate.cozymate_server.domain.roomfavorite.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.roomfavorite.RoomFavorite;
import com.cozymate.cozymate_server.domain.roomfavorite.converter.RoomFavoriteConverter;
import com.cozymate.cozymate_server.domain.roomfavorite.repository.RoomFavoriteRepositoryService;
import com.cozymate.cozymate_server.domain.roomfavorite.validator.RoomFavoriteValidator;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomFavoriteCommandService {

    private final RoomRepository roomRepository;
    private final RoomFavoriteValidator roomFavoriteValidator;
    private final RoomFavoriteRepositoryService roomFavoriteRepositoryService;

    public void saveRoomFavorite(Member member, Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(
            () -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND)
        );

        roomFavoriteValidator.checkRoomCanBeFavorited(room);
        roomFavoriteValidator.checkDuplicateRoomFavorite(member, room);

        roomFavoriteRepositoryService.createRoomFavorite(RoomFavoriteConverter.toEntity(member, room));
    }

    public void deleteRoomFavorite(Member member, Long roomFavoriteId) {
        RoomFavorite roomFavorite = roomFavoriteRepositoryService.getRoomFavoriteByIdOrThrow(roomFavoriteId);

        roomFavoriteValidator.checkDeletePermission(roomFavorite, member);

        roomFavoriteRepositoryService.deleteRoomFavorite(roomFavorite);
    }
}
