package com.cozymate.cozymate_server.domain.roomfavorite.validator;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.roomfavorite.RoomFavorite;
import com.cozymate.cozymate_server.domain.roomfavorite.repository.RoomFavoriteRepositoryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomFavoriteValidator {

    private final RoomFavoriteRepositoryService roomFavoriteRepositoryService;

    public void checkRoomCanBeFavorited(Room room) {
        if (RoomType.PRIVATE.equals(room.getRoomType())) {
            throw new GeneralException(ErrorStatus._ROOMFAVORITE_CANNOT_PRIVATE_ROOM);
        }

        if (room.getNumOfArrival() == room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOMFAVORITE_CANNOT_FULL_ROOM);
        }

        if (RoomStatus.DISABLE.equals(room.getStatus())) {
            throw new GeneralException(ErrorStatus._ROOMFAVORITE_CANNOT_DISABLE_ROOM);
        }
    }

    public void checkDuplicateRoomFavorite(Member member, Room room) {
        if (roomFavoriteRepositoryService.existRoomFavoriteByMemberAndRoom(member, room)) {
            throw new GeneralException(ErrorStatus._ROOMFAVORITE_ALREADY_EXISTS);
        }
    }

    public void checkDeletePermission(RoomFavorite roomFavorite, Member member) {
        if (!roomFavorite.getMember().getId().equals(member.getId())) {
            throw new GeneralException(ErrorStatus._ROOMFAVORITE_MEMBER_MISMATCH);
        }
    }
}
