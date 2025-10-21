package com.cozymate.cozymate_server.domain.room.validator;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepositoryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.repository.MemberStatRepositoryService;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepositoryService;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomValidator {

    private final MateRepositoryService mateRepositoryService;
    private final RoomRepositoryService roomRepositoryService;

    private final MemberStatRepositoryService memberStatRepositoryService;

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

    public Mate validateInviteStatus(Long roomId, Long inviteeId) {
        return mateRepositoryService.getMateByRoomIdAndMemberIdAndStatus(
                roomId, inviteeId, EntryStatus.INVITED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._INVITATION_NOT_FOUND));
    }

    // 참여 요청(PENDING) 상태인지 검사
    public Mate validatePendingStatus(Long roomId, Long requesterId) {
        return mateRepositoryService.getMateByRoomIdAndMemberIdAndStatus(
                roomId, requesterId, EntryStatus.PENDING)
            .orElseThrow(() -> new GeneralException(ErrorStatus._REQUEST_NOT_FOUND));
    }

    // 사용자 상세정보 여부 검사
    public void checkMemberStatIsNull(Long memberId){
        memberStatRepositoryService.getMemberStatOrThrow(memberId);
    }

    // 방 성별 검사
    public void checkGender(Room room, Member member){
        if(!room.getGender().equals(member.getGender())){
            throw new GeneralException(ErrorStatus._MISMATCH_GENDER);
        }
    }

    public void checkUniversity(University university, Member member){
        if (!member.getUniversity().equals(university)) {
            throw new GeneralException(ErrorStatus._MISMATCH_UNIVERSITY);
        }
    }

    // 비공개방인지 검사
    public void checkPrivateRoom(Room room) {
        if (room.getRoomType() != RoomType.PRIVATE) {
            throw new GeneralException(ErrorStatus._PUBLIC_ROOM);
        }
    }

    // 공개방인지 검사
    public void checkPublicRoom(Room room) {
        if (room.getRoomType() != RoomType.PUBLIC) {
            throw new GeneralException(ErrorStatus._PRIVATE_ROOM);
        }
    }

    // 이미 나간 방에 대한 예외 처리
    public void checkNotExited(Mate mate) {
        if (mate.getEntryStatus() == EntryStatus.EXITED) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MATE);
        }
    }

    public void checkNotSelfForcedQuit(Member manager, Long targetMemberId) {
        if (manager.getId().equals(targetMemberId)) {
            throw new GeneralException(ErrorStatus._CANNOT_SELF_FORCED_QUIT);
        }
    }

}
