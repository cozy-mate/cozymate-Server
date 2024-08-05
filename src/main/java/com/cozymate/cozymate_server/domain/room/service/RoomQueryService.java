package com.cozymate.cozymate_server.domain.room.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.room.dto.RoomCreateResponse;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomQueryService {

    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;

    public RoomCreateResponse getRoomById(Long roomId, Long memberId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        mateRepository.findByRoomIdAndMemberId(roomId, memberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));

        Mate manager = mateRepository.findByRoomIdAndIsRoomManager(room.getId(), true)
            .orElseThrow(()-> new GeneralException(ErrorStatus._ROOM_MANAGER_NOT_FOUND));
        if (!manager.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus._NOT_ROOM_MANAGER);
        }

        return new RoomCreateResponse(room.getName(), room.getInviteCode(), room.getProfileImage());
    }

}
