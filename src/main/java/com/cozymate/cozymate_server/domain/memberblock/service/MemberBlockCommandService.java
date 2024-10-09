package com.cozymate.cozymate_server.domain.memberblock.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberblock.MemberBlock;
import com.cozymate.cozymate_server.domain.memberblock.converter.MemberBlockConverter;
import com.cozymate.cozymate_server.domain.memberblock.dto.MemberBlockRequestDto;
import com.cozymate.cozymate_server.domain.memberblock.repository.MemberBlockRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberBlockCommandService {

    private final MemberBlockRepository memberBlockRepository;
    private final MemberRepository memberRepository;

    public void saveMemberBlock(MemberBlockRequestDto requestDto, Member member) {
        if (requestDto.getBlockedMemberId().equals(member.getId())) {
            throw new GeneralException(ErrorStatus._CANNOT_BLOCK_OWN);
        }

        checkDuplicatedBlock(requestDto, member);

        Member blockedMember = memberRepository.findById(requestDto.getBlockedMemberId())
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        MemberBlock memberBlock = MemberBlockConverter.toEntity(member, blockedMember);

        memberBlockRepository.save(memberBlock);
    }

    public void deleteMemberBlock(Long blockedMemberId, Member member) {
        MemberBlock memberBlock = memberBlockRepository.findByMemberIdAndBlockedMemberId(
                member.getId(), blockedMemberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ALREADY_NOT_BLOCKED_MEMBER));

        memberBlockRepository.delete(memberBlock);
    }

    private void checkDuplicatedBlock(MemberBlockRequestDto requestDto, Member member) {
        boolean alreadyBlocked = memberBlockRepository.existsByMemberIdAndBlockedMemberId(member.getId(),
            requestDto.getBlockedMemberId());

        if (alreadyBlocked) {
            throw new GeneralException(ErrorStatus._ALREADY_BLOCKED_MEMBER);
        }
    }
}