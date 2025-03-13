package com.cozymate.cozymate_server.domain.memberfavorite.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberfavorite.MemberFavorite;
import com.cozymate.cozymate_server.domain.memberfavorite.converter.MemberFavoriteConverter;
import com.cozymate.cozymate_server.domain.memberfavorite.repository.MemberFavoriteRepositoryService;
import com.cozymate.cozymate_server.domain.memberfavorite.validator.MemberFavoriteValidator;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberFavoriteCommandService {

    private final MemberRepository memberRepository;
    private final MemberFavoriteRepositoryService memberFavoriteRepositoryService;
    private final MemberFavoriteValidator memberFavoriteValidator;

    public void saveMemberFavorite(Member member, Long targetMemberId) {
        memberFavoriteValidator.checkSameMember(member, targetMemberId);

        Member targetMember = memberRepository.findById(targetMemberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        memberFavoriteValidator.checkMemberStatIsNull(targetMember);
        memberFavoriteValidator.checkDuplicateMemberFavorite(member, targetMember);

        memberFavoriteRepositoryService.createMemberFavorite(
            MemberFavoriteConverter.toEntity(member, targetMember));
    }

    public void deleteMemberFavorite(Member member, Long memberFavoriteId) {
        MemberFavorite memberFavorite = memberFavoriteRepositoryService.getMemberFavoriteByIdOrThrow(
            memberFavoriteId);

        memberFavoriteValidator.checkDeletePermission(memberFavorite, member);

        memberFavoriteRepositoryService.deleteMemberFavorite(memberFavorite);
    }
}
