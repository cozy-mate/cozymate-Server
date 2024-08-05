package com.cozymate.cozymate_server.domain.member.service;

import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.utils.MemberDetails;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.member.dto.MemberRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberCommandService {
    private final AuthService authService;
    private final MemberQueryService memberQueryService;

    public Boolean checkNickname(String nickname) {
        return !memberQueryService.isValidNickName(nickname);
    }

    public MemberDetails join(String clientId, MemberRequestDTO.JoinRequestDTO joinRequestDTO) {
        Member member = MemberConverter.toMember(clientId, joinRequestDTO);
        memberQueryService.save(member);
        log.info(member.getNickname() + "저장 완료");
        return new MemberDetails(member);
    }

    public HttpHeaders makeHeader(MemberDetails memberDetails) {
        String token = authService.generateToken(memberDetails.getMember().getClientId());
        return authService.addTokenAtHeader(token);
    }

    public MemberResponseDTO.LoginResponseDTO makeBody(MemberDetails memberDetails) {
        return MemberConverter.toLoginResponseDTO(memberDetails.getMember().getNickname(),
                authService.getRefreshToken(memberDetails));
    }

    public MemberResponseDTO.MemberInfoDTO getMemberInfo(MemberDetails memberDetails) {
        return MemberConverter.toMemberInfoDTO(memberDetails.getMember());
    }
}
