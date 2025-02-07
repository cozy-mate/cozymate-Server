package com.cozymate.cozymate_server.domain.roommemberstat.service;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.DifferenceStatus;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.FieldInstanceResolver;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.QuestionAnswerMapper;
import com.cozymate.cozymate_server.domain.room.Room;
import com.cozymate.cozymate_server.domain.roommemberstat.converter.RoomMemberStatDetailConverter;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomMemberStatDetailDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomMemberStatDetailListDTO;
import com.cozymate.cozymate_server.domain.room.enums.RoomType;
import com.cozymate.cozymate_server.domain.room.repository.RoomRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomMemberStatService {

    private final RoomRepository roomRepository;
    private final MateRepository mateRepository;


    public RoomMemberStatDetailListDTO getRoomMemberStatDetailList(Long roomId,
        String memberStatAttribute) {

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ROOM_NOT_FOUND));

        if (room.getRoomType().equals(RoomType.PRIVATE)) {
            throw new GeneralException(ErrorStatus._PRIVATE_ROOM);
        }

        List<Member> joinedMemberList = mateRepository.findAllByRoomIdAndEntryStatus(roomId,
                EntryStatus.JOINED)
            .stream()
            .map(Mate::getMember)
            .toList();

        DifferenceStatus color = MemberStatConverter.toDifferenceStatus(
            joinedMemberList.stream().map(
                    Member::getMemberStat)
                .toList(),
            memberStatAttribute
        );

        List<RoomMemberStatDetailDTO> roomMemberStat = joinedMemberList.stream().map(
            joinedMember -> {
                Map<String, Object> rawStatMap = new HashMap<>();
                Object stat = FieldInstanceResolver.extractMemberStatField(
                    joinedMember.getMemberStat(), memberStatAttribute
                );
                rawStatMap.put(memberStatAttribute, stat);
                return RoomMemberStatDetailConverter.toRoomMemberStatDetailDTO(
                    joinedMember, QuestionAnswerMapper.convertToStringMap(rawStatMap)
                );
            }
        ).toList();

        return RoomMemberStatDetailConverter.toRoomMemberStatDetailListDTO(roomMemberStat, color);
    }

}
