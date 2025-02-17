package com.cozymate.cozymate_server.domain.memberfavorite.converter;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberfavorite.MemberFavorite;
import com.cozymate.cozymate_server.domain.memberfavorite.dto.response.MemberFavoriteResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.response.MemberStatPreferenceResponseDTO;

public class MemberFavoriteConverter {

    public static MemberFavorite toEntity(Member member, Member targetMember) {
        return MemberFavorite.builder()
            .member(member)
            .targetMember(targetMember)
            .build();
    }

    public static MemberFavoriteResponseDTO toMemberFavoriteResponseDTO(Long memberFavoriteId,
        MemberStatPreferenceResponseDTO memberStatPreferenceResponseDTO) {
        return MemberFavoriteResponseDTO.builder()
            .memberFavoriteId(memberFavoriteId)
            .memberStatPreferenceDetail(memberStatPreferenceResponseDTO)
            .build();
    }
}
