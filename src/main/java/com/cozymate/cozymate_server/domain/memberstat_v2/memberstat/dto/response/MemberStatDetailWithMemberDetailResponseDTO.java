package com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.dto.response;

import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import lombok.Builder;

@Builder
public record MemberStatDetailWithMemberDetailResponseDTO(
    MemberDetailResponseDTO memberDetail,
    MemberStatDetailResponseDTO memberStatDetail
) {

}
