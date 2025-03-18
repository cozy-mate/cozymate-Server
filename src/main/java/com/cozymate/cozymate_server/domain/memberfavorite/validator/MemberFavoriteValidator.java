package com.cozymate.cozymate_server.domain.memberfavorite.validator;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberfavorite.MemberFavorite;
import com.cozymate.cozymate_server.domain.memberfavorite.repository.MemberFavoriteRepositoryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberFavoriteValidator {

    private final MemberFavoriteRepositoryService memberFavoriteRepositoryService;

    public void checkSameMember(Member member, Long targetMemberId) {
        if (targetMemberId.equals(member.getId())) {
            throw new GeneralException(ErrorStatus._MEMBERFAVORITE_CANNOT_REQUEST_SELF);
        }
    }

    public void checkMemberStatIsNull(Member member) {
        if (Objects.isNull(member.getMemberStat())) {
            throw new GeneralException(
                ErrorStatus._MEMBERFAVORITE_CANNOT_FAVORITE_MEMBER_WITHOUT_MEMBERSTAT);
        }
    }

    public void checkDuplicateMemberFavorite(Member member, Member targetMember) {
        if (memberFavoriteRepositoryService.existMemberFavoriteByMemberAndTargetMember(member,
            targetMember)) {
            throw new GeneralException(ErrorStatus._MEMBERFAVORITE_ALREADY_EXISTS);
        }
    }

    public void checkDeletePermission(MemberFavorite memberFavorite, Member member) {
        if (!memberFavorite.getMember().getId().equals(member.getId())) {
            throw new GeneralException(ErrorStatus._MEMBERFAVORITE_MEMBER_MISMATCH);
        }
    }
}
