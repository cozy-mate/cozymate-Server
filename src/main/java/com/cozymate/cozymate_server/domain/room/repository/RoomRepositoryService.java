package com.cozymate.cozymate_server.domain.room.repository;

import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.enums.RoomStatus;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomRepositoryService {

    private final RoomRepository roomRepository;

    // 방 ID로 조회
    public Room getRoomOrThrow(Long roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));
    }

    // 초대 코드로 방 조회
    public Room getRoomByInviteCodeOrThrow(String inviteCode) {
        return roomRepository.findByInviteCode(inviteCode)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));
    }

    // 방 이름 중복 검사
    public boolean existsByRoomName(String roomName) {
        return roomRepository.existsByName(roomName);
    }

    public List<Room> getRoomsWithMates(Long memberId, EntryStatus entryStatus) {
        return roomRepository.findRoomsWithMates(memberId, entryStatus);
    }

    public List<Room> getMatchingPublicRooms(String keyword, Long universityId, Gender gender) {
        return roomRepository.findMatchingPublicRooms(keyword, universityId, gender);
    }

    // 초대 코드 중복 체크
    public boolean existsRoomByInviteCode(String inviteCode) {
        return roomRepository.existsByInviteCode(inviteCode);
    }

    // 방에 참여중인지 검사
    public boolean existsByMemberIdAndStatuses(Long memberId) {
        return roomRepository.existsByMemberIdAndStatuses(memberId, RoomStatus.ENABLE,
            RoomStatus.WAITING, EntryStatus.JOINED);
    }

    // 방 저장
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    // 방 삭제
    public void delete(Room room) {
        roomRepository.delete(room);
    }

}
