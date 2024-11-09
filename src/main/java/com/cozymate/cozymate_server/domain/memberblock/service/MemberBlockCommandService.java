package com.cozymate.cozymate_server.domain.memberblock.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberblock.MemberBlock;
import com.cozymate.cozymate_server.domain.memberblock.converter.MemberBlockConverter;
import com.cozymate.cozymate_server.domain.memberblock.dto.request.MemberBlockRequestDTO;
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

    public void saveMemberBlock(MemberBlockRequestDTO memberBlockRequestDTO, Member member) {
        Long blockedMemberId = memberBlockRequestDTO.memberId();

        checkBlockSelf(blockedMemberId, member.getId());
        checkDuplicatedBlock(blockedMemberId, member.getId());

        Member blockedMember = memberRepository.findById(blockedMemberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        MemberBlock memberBlock = MemberBlockConverter.toEntity(member, blockedMember);

        memberBlockRepository.save(memberBlock);
    }

    public void deleteMemberBlock(Long blockedMemberId, Member member) {
        checkBlockSelf(blockedMemberId, member.getId());

        MemberBlock memberBlock = memberBlockRepository.findByMemberIdAndBlockedMemberId(
                member.getId(), blockedMemberId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._ALREADY_NOT_BLOCKED_MEMBER));

        memberBlockRepository.delete(memberBlock);
    }

    private void checkBlockSelf(Long blockedMemberId, Long memberId) {
        if (blockedMemberId.equals(memberId)) {
            throw new GeneralException(ErrorStatus._CANNOT_BLOCK_REQUEST_SELF);
        }
    }

    private void checkDuplicatedBlock(Long blockedMemberId, Long memberId) {
        boolean alreadyBlocked = memberBlockRepository.existsByMemberIdAndBlockedMemberId(memberId,
            blockedMemberId);

        if (alreadyBlocked) {
            throw new GeneralException(ErrorStatus._ALREADY_BLOCKED_MEMBER);
        }
    }
}