package com.cozymate.cozymate_server.domain.mate.repository;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MateRepositoryService {

    private final MateRepository mateRepository;

    // 특정 방의 특정 멤버 조회
    public Optional<Mate> getMateOptional(Long roomId, Long memberId) {
        return mateRepository.findByRoomIdAndMemberId(roomId, memberId);
    }

    public Mate getJoinedMateOrThrow(Long roomId, Long memberId) {
        return mateRepository.findByRoomIdAndMemberIdAndEntryStatus(roomId, memberId, EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));
    }

    public Mate getJoinedMateFetchMemberOrThrow(Long roomId, Long memberId) {
        return mateRepository.findFetchMemberByRoomIdAndMemberIdAndEntryStatus(roomId, memberId, EntryStatus.JOINED)
            .orElseThrow(() -> new GeneralException(ErrorStatus._NOT_ROOM_MATE));
    }

}
