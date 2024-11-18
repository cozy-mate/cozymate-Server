package com.cozymate.cozymate_server.domain.roomlog.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.roomlog.RoomLog;
import com.cozymate.cozymate_server.domain.roomlog.converter.RoomLogConverter;
import com.cozymate.cozymate_server.domain.roomlog.dto.RoomLogResponseDto.RoomLogDetailResponseDto;
import com.cozymate.cozymate_server.domain.roomlog.repository.RoomLogRepository;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomLogQueryService {

    private final RoomLogRepository roomLogRepository;
    private final MateRepository mateRepository;


    public PageResponseDto<List<RoomLogDetailResponseDto>> getRoomLogList(
        Long roomId,
        Member member,
        int page,
        int size) {

        // 본인 방에 속한건지 확인
        Mate mate = mateRepository.findByMemberIdAndRoomId(member.getId(), roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MATE_OR_ROOM_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);

        Slice<RoomLog> roomLogSlice = roomLogRepository.findAllByRoomIdOrderByCreatedAtDesc(roomId,
            pageable);

        List<RoomLogDetailResponseDto> roomLogResponseList = roomLogSlice.getContent().stream()
            .map(RoomLogConverter::toRoomLogDetailResponseDto)
            .toList();

        return PageResponseDto.<List<RoomLogDetailResponseDto>>builder()
            .page(pageable.getPageNumber())
            .hasNext(roomLogSlice.hasNext())
            .result(roomLogResponseList)
            .build();
    }

}
