package com.cozymate.cozymate_server.domain.memberstat.memberstat.util;


import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.DifferenceStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class MemberStatComparator {

    public static <T> DifferenceStatus compareField(
        List<MemberStat> memberStatList,
        BiFunction<Member, MemberStat, T> getter) {

        boolean foundSame = false;
        boolean foundDifferent = false;

        // 리스트 내 모든 값 비교
        for (int i = 0; i < memberStatList.size(); i++) {
            T currentValue = getter.apply(memberStatList.get(i).getMember(), memberStatList.get(i));
            for (int j = i + 1; j < memberStatList.size(); j++) {
                T comparisonValue = getter.apply(memberStatList.get(j).getMember(),
                    memberStatList.get(j));
                if (currentValue.equals(comparisonValue)) {
                    foundSame = true;
                } else {
                    foundDifferent = true;
                }

                if (foundSame && foundDifferent) {
                    return DifferenceStatus.NOT_SAME_NOT_DIFFERENT;
                }
            }
        }

        if (foundSame) {
            return DifferenceStatus.SAME;
        } else {
            return DifferenceStatus.DIFFERENT;
        }
    }

    public static DifferenceStatus compareField(
        Object memberStatMapValue, Object criteriaMemberStatValue) {
        if (memberStatMapValue == null || criteriaMemberStatValue == null) {
            return DifferenceStatus.NOT_SAME_NOT_DIFFERENT;
        }

        String targetValue = memberStatMapValue.toString();
        String criteriaValue = criteriaMemberStatValue.toString();

        if (targetValue.equals(criteriaValue)) {
            return DifferenceStatus.SAME;
        }

        // 다중 값 비교를 위한 처리
        if (targetValue.contains(",") || criteriaValue.contains(",")) {
            Set<String> set1 = new HashSet<>(List.of(targetValue.split(",")));
            Set<String> set2 = new HashSet<>(List.of(criteriaValue.split(",")));

            Set<String> intersection = new HashSet<>(set1);
            intersection.retainAll(set2);

            if (intersection.isEmpty()) {
                return DifferenceStatus.DIFFERENT;
            } else {
                return DifferenceStatus.NOT_SAME_NOT_DIFFERENT;
            }
        }

        return DifferenceStatus.DIFFERENT;
    }
}
