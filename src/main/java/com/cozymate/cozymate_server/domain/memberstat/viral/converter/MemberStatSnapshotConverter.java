package com.cozymate.cozymate_server.domain.memberstat.viral.converter;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.viral.MemberStatSnapshot;
import com.cozymate.cozymate_server.domain.memberstat.viral.dto.CreateMemberStatSnapshotRequestDTO;

public class MemberStatSnapshotConverter {
    public static MemberStatSnapshot toEntity(CreateMemberStatSnapshotRequestDTO dto){
        Lifestyle lifestyle = MemberStatConverter.toLifestyleFromDto(dto);

        return MemberStatSnapshot.builder()
            .lifestyle(lifestyle)
            .build();
    }
}
