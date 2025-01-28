package com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.util;


import com.cozymate.cozymate_server.domain.memberstat_v2.memberstat.enums.DifferenceStatus;

public class MemberStatComparator {

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
