package com.cozymate.cozymate_server.domain.roommemberstat.converter;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.DifferenceStatus;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomMemberStatDetailDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomMemberStatDetailListDTO;
import java.util.List;
import java.util.Map;

public class RoomMemberStatDetailConverter {

    public static RoomMemberStatDetailDTO toRoomMemberStatDetailDTO (Member member, Map<String, String> stat){
        return RoomMemberStatDetailDTO.builder()
            .memberDetail(MemberConverter.toMemberDetailResponseDTOFromEntity(member))
            .memberStat(stat)
            .build();
    }

    public static RoomMemberStatDetailListDTO toRoomMemberStatDetailListDTO (List<RoomMemberStatDetailDTO> roomMemberStatDetailDTOList, DifferenceStatus color){
        return RoomMemberStatDetailListDTO.builder()
            .memberList(roomMemberStatDetailDTOList)
            .color(color.getValue())
            .build();
    }

}
