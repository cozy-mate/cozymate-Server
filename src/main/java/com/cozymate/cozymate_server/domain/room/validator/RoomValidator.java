package com.cozymate.cozymate_server.domain.room.validator;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepositoryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomValidator {

    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;
    private final RoomRepositoryService roomRepositoryService;

    // 방 이름 중복 검사
    public Boolean isValidRoomName(String roomName) {
        return !roomRepositoryService.existsByRoomName(roomName);
    }

    // 방 접근 권한 검사
    public void checkRoomAccess(Room room, Long memberId) {
        if (room.getRoomType() == RoomType.PRIVATE) {
            checkRoomMember(room.getId(), memberId);
        }
    }

    // 방에 속한 멤버인지 검사
    public Mate checkRoomMember(Long roomId, Long memberId) {
        return mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId, EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));
    }

    // 방장인지 검사
    public Mate checkRoomManager(Long roomId, Long managerId) {
        Mate manager = mateRepository.findByRoomIdAndIsRoomManager(roomId, true)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));

        if (!manager.getMember().getId().equals(managerId)) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }
        return manager;
    }

    // 꽉찬 방인지 검사
    public void isRoomFull(Room room) {
        if (room.getNumOfArrival() >= room.getMaxMateNum()) {
            throw new GeneralException(ErrorStatus._ROOM_FULL);
        }
    }

    // 이미 참여한 방이 있는지 검사
    public void isAlreadyJoinedRoom(Long memberId) {
        if (roomRepositoryService.existsByMemberIdAndStatuses(memberId)) {
            throw new GeneralException(ErrorStatus._ROOM_ALREADY_EXISTS);
        }
    }

    // 방 entryStatus 검사
    public void checkEntryStatus(Optional<Mate> existingMate) {
        if (existingMate.isPresent()) {
            EntryStatus status = existingMate.get().getEntryStatus();
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

}
