package com.cozymate.cozymate_server.domain.roomfavorite.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.roomfavorite.RoomFavorite;
import com.cozymate.cozymate_server.domain.roomfavorite.converter.RoomFavoriteConverter;
import com.cozymate.cozymate_server.domain.roomfavorite.repository.RoomFavoriteRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomFavoriteCommandService {

    private final RoomFavoriteRepository roomFavoriteRepository;
    private final RoomRepository roomRepository;

    public void saveRoomFavorite(Member member, Long roomId) {
        Room room = validRoom(roomId);
        checkDuplicateFavorite(member, room);

        roomFavoriteRepository.save(RoomFavoriteConverter.toEntity(member, room));
    }

    public void deleteRoomFavorite(Member member, Long roomFavoriteId) {
        RoomFavorite roomFavorite = roomFavoriteRepository.findById(roomFavoriteId).orElseThrow(
            () -> new GeneralException(ErrorStatus._ROOMFAVORITE_NOT_FOUND)
        );

        if (!roomFavorite.getMember().getId().equals(member.getId())) {
            throw new GeneralException(ErrorStatus._ROOMFAVORITE_MEMBER_MISMATCH);
        }

        roomFavoriteRepository.delete(roomFavorite);
    }

    private Room validRoom(Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(
            () -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND)
        );

        if (RoomType.PRIVATE.equals(room.getRoomType())) {
            throw new GeneralException(ErrorStatus._ROOMFAVORITE_CANNOT_PRIVATE_ROOM);
        }

        if (room.getNumOfArrival() == room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOMFAVORITE_CANNOT_FULL_ROOM);
        }

        if (RoomStatus.DISABLE.equals(room.getStatus())) {
            throw new GeneralException(ErrorStatus._ROOMFAVORITE_CANNOT_DISABLE_ROOM);
        }

        return room;
    }

    private void checkDuplicateFavorite(Member member, Room room) {
        boolean isExists = roomFavoriteRepository.existsByMemberAndRoom(member, room);

        if (isExists) {
            throw new GeneralException(ErrorStatus._ROOMFAVORITE_ALREADY_EXISTS);
        }
    }
}
