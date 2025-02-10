package com.cozymate.cozymate_server.domain.memberfavorite.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.memberfavorite.MemberFavorite;
import com.cozymate.cozymate_server.domain.memberfavorite.converter.MemberFavoriteConverter;
import com.cozymate.cozymate_server.domain.memberfavorite.repository.MemberFavoriteRepository;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberFavoriteCommandService {

    private final MemberFavoriteRepository memberFavoriteRepository;
    private final MemberRepository memberRepository;

    public void saveMemberFavorite(Member member, Long targetMemberId) {
        if (targetMemberId.equals(member.getId())) {
            throw new GeneralException(ErrorStatus._MEMBERFAVORITE_CANNOT_REQUEST_SELF);
        }

        Member targetMember = validMember(targetMemberId);

        if (memberFavoriteRepository.existsByMemberAndTargetMember(member, targetMember)) {
            throw new GeneralException(ErrorStatus._MEMBERFAVORITE_ALREADY_EXISTS);
        }

        memberFavoriteRepository.save(MemberFavoriteConverter.toEntity(member, targetMember));
    }

    public void deleteMemberFavorite(Member member, Long memberFavoriteId) {
        MemberFavorite memberFavorite = memberFavoriteRepository.findById(memberFavoriteId)
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERFAVORITE_NOT_FOUND));

        if (!memberFavorite.getMember().getId().equals(member.getId())) {
            throw new GeneralException(ErrorStatus._MEMBERFAVORITE_MEMBER_MISMATCH);
        }

        memberFavoriteRepository.delete(memberFavorite);
    }

    private Member validMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
            () -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        if (Objects.isNull(member.getMemberStat())) {
            throw new GeneralException(ErrorStatus._MEMBERFAVORITE_CANNOT_FAVORITE_MEMBER_WITHOUT_MEMBERSTAT);
        }

        return member;
    }
}
