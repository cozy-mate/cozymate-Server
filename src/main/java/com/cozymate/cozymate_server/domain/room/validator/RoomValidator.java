package com.cozymate.cozymate_server.domain.room.validator;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepositoryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepositoryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomValidator {

    private final MateRepositoryService mateRepositoryService;
    private final RoomRepositoryService roomRepositoryService;

    // 방 이름 중복 검사
    public Boolean isUniqueRoomName(String roomName) {
        return !roomRepositoryService.getRoomNameExists(roomName);
    }

    // 방 접근 권한 검사
    public void checkRoomAccess(Room room, Long memberId) {
        if (room.getRoomType() == RoomType.PRIVATE) {
            mateRepositoryService.getJoinedMateOrThrow(room.getId(), memberId);
        }
    }

    // 방장인지 검사
    public void checkRoomManager(Mate mate) {
        if (!mate.isRoomManager()) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }
    }

    // 꽉찬 방인지 검사
    public void checkRoomFull(Room room) {
        if (room.getNumOfArrival() >= room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOM_FULL);
        }
    }

    // 이미 참여한 방이 있는지 검사
    public void checkAlreadyJoinedRoom(Long memberId) {
        if (roomRepositoryService.getRoomParticipationExistsByMemberId(memberId)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }
    }

    // 방 entryStatus 검사
    public void checkEntryStatus(Mate mate) {
        EntryStatus status = mate.getEntryStatus();
        switch (status) {
            case JOINED:
                throw new GeneralException(ErrorStatus._ROOM_ALREADY_JOINED);
            case PENDING:
                throw new GeneralException(ErrorStatus._REQUEST_ALREADY_SENT);
            case INVITED:
                throw new GeneralException(ErrorStatus._INVITATION_ALREADY_SENT);
            default:
                break;
        }
    }

}
