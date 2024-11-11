package com.cozymate.cozymate_server.domain.memberblock.converter;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberblock.MemberBlock;
import com.cozymate.cozymate_server.domain.memberblock.dto.response.MemberBlockResponseDTO;

public class MemberBlockConverter {

    public static MemberBlock toEntity(Member member, Member blockedMember) {
        return MemberBlock.builder()
            .member(member)
            .blockedMember(blockedMember)
            .build();
    }

    public static MemberBlockResponseDTO toMemberBlockResponseDTO(Member blockedMember) {
        return MemberBlockResponseDTO.builder()
            .memberId(blockedMember.getId())
            .nickname(blockedMember.getNickname())
            .build();
    }
}