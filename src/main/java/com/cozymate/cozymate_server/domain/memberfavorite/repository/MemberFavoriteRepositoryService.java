package com.cozymate.cozymate_server.domain.memberfavorite.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberfavorite.MemberFavorite;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberFavoriteRepositoryService {

    private final MemberFavoriteRepository memberFavoriteRepository;

    public boolean existMemberFavoriteByMemberAndTargetMember(Member member, Member targetMember) {
        return memberFavoriteRepository.existsByMemberAndTargetMember(member, targetMember);
    }

    public void createMemberFavorite(MemberFavorite memberFavorite) {
        memberFavoriteRepository.save(memberFavorite);
    }

    public MemberFavorite getMemberFavoriteByIdOrThrow(Long memberFavoriteId) {
        return memberFavoriteRepository.findById(memberFavoriteId)
            .orElseThrow(
                () -> new GeneralException(ErrorStatus._MEMBERFAVORITE_NOT_FOUND)
            );
    }

    public void deleteMemberFavorite(MemberFavorite memberFavorite) {
        memberFavoriteRepository.delete(memberFavorite);
    }

    public List<MemberFavorite> getMemberFavoriteListByMember(Member member) {
        return memberFavoriteRepository.findByMember(member);
    }
}
