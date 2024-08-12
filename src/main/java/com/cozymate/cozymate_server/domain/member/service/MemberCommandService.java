package com.cozymate.cozymate_server.domain.member.service;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.service.AuthService;
import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.converter.MemberConverter;
import com.cozymate.cozymate_server.domain.member.dto.MemberRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberCommandService {
    private final AuthService authService;
    private final MemberQueryService memberQueryService;
    private final MemberRepository memberRepository;

    public Boolean checkNickname(String nickname) {
        return !memberQueryService.isValidNickName(nickname);
    }

    public MemberDetails join(String clientId, MemberRequestDTO.JoinRequestDTO joinRequestDTO) {
        Member member = MemberConverter.toMember(clientId, joinRequestDTO);

        member = memberRepository.save(member);

        return new MemberDetails(member);
    }

    public MemberDetails extractMemberDetailsByRefreshToken(String refreshToken) {
        return authService.extractMemberDetailsInRefreshToken(refreshToken);
    }

    public AuthResponseDTO.TokenResponseDTO generateTokenDTO(MemberDetails memberDetails) {
        return authService.generateTokenDTO(memberDetails.getUsername());
    }

    public MemberResponseDTO.MemberInfoDTO getMemberInfo(MemberDetails memberDetails) {
        return MemberConverter.toMemberInfoDTO(memberDetails.getMember());
    }

    public void withdraw(MemberDetails memberDetails) {
        memberRepository.delete(memberDetails.getMember());
    }
}