package com.cozymate.cozymate_server.domain.memberstat.dto.response;

import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import lombok.Builder;

@Builder
public record MemberStatDetailWithMemberDetailDTO(
    MemberDetailResponseDTO memberDetail,
    MemberStatDetailResponseDTO memberStatDetail
) {

}
