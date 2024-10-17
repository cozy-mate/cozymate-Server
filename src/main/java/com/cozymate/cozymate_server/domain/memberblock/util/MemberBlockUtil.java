package com.cozymate.cozymate_server.domain.memberblock.util;

import com.cozymate.cozymate_server.domain.mate.repository.MateRepository;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberblock.repository.MemberBlockRepository;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberBlockUtil {

    private final MemberBlockRepository memberBlockRepository;
    private final MateRepository mateRepository;

    /**
     * @param member          : 로그인 사용자
     * @param blockedMemberId : 차단된 사용자인지 확인 대상 멤버id
     * @return 차단된 사용자 : true, 차단되지 않은 사용자 : false
     */
    public boolean existsMemberBlock(Member member, Long blockedMemberId) {
        return memberBlockRepository.existsByMemberIdAndBlockedMemberId(member.getId(),
            blockedMemberId);
    }

    /**
     *
     * @param targetList : memberId를 가지는 클래스 타입의 리스트
     * @param member : 로그인 사용자
     * @param idExtractor : memberId를 얻을 수 있는 메서드 참조
     * @return : 차단된 사용자에 대해 필터링된 결과
     */
    public <T> List<T> filterBlockedMember(List<T> targetList, Member member,
        Function<T, Long> idExtractor) {
        Set<Long> blockedMemberIds = getBlockedMemberIds(member);

        if (blockedMemberIds.isEmpty()) {
            return targetList;
        }

        return filterByBlockedIds(targetList, blockedMemberIds, idExtractor);
    }

    /**
     *
     * @param targetList : mateId를 가지는 클래스 타입의 리스트
     * @param member : 로그인 사용자
     * @param idExtractor : mateId를 얻을 수 있는 메서드 참조
     * @return : 차단된 사용자에 대해 필터링된 결과
     */
    public <T> List<T> filterBlockedMate(List<T> targetList, Member member,
        Function<T, Long> idExtractor) {
        Set<Long> blockedMemberIds = getBlockedMemberIds(member);

        if (blockedMemberIds.isEmpty()) {
            return targetList;
        }

        Set<Long> blockedMateIds = mateRepository.findMateIdsByMemberIds(blockedMemberIds);

        return filterByBlockedIds(targetList, blockedMateIds, idExtractor);
    }

    private Set<Long> getBlockedMemberIds(Member member) {
        return memberBlockRepository.findBlockedMemberIdsByMember(member);
    }

    private <T> List<T> filterByBlockedIds(List<T> targetList, Set<Long> blockedIds,
        Function<T, Long> idExtractor) {
        return targetList.stream()
            .filter(target -> !blockedIds.contains(idExtractor.apply(target)))
            .toList();
    }
}