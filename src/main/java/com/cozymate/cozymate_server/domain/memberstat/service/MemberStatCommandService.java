package com.cozymate.cozymate_server.domain.memberstat.service;

import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;

public interface MemberStatCommandService {
    Long createMemberStat(Long memberId, MemberStatRequestDTO memberStatRequestDTO);

}
