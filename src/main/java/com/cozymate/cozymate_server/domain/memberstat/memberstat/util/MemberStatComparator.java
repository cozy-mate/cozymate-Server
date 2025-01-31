package com.cozymate.cozymate_server.domain.memberstat.memberstat.util;


import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.enums.DifferenceStatus;
import java.util.List;
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
                T comparisonValue = getter.apply(memberStatList.get(j).getMember(), memberStatList.get(j));
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
        if (memberStatMapValue.equals(criteriaMemberStatValue)) {
            return DifferenceStatus.SAME;
        }
        return DifferenceStatus.DIFFERENT;
    }
}
