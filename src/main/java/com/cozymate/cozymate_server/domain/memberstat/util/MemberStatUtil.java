package com.cozymate.cozymate_server.domain.memberstat.util;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatDifferenceResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.DifferenceStatus;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MemberStatUtil {

    public static String toSortedString(List<String> list) {
        return list.stream()
            .sorted()
            .collect(Collectors.joining(","));
    }

    public static List<String> fromStringToList(String str) {
        return Arrays.stream(str.replaceAll(",$", "").split(","))
            .collect(Collectors.toList());
    }

    public static <T> DifferenceStatus compareField(List<MemberStat> memberStatList, java.util.function.Function<MemberStat, T> getter) {
        boolean foundSame = false;
        boolean foundDifferent = false;

        T firstValue = getter.apply(memberStatList.get(0));

        for (int i = 1; i < memberStatList.size(); i++) {
            T currentValue = getter.apply(memberStatList.get(i));

            if (firstValue.equals(currentValue)) {
                foundSame = true;
            } else {
                foundDifferent = true;
            }

            if (foundSame && foundDifferent) {
                return DifferenceStatus.WHITE;
            }
        }
        if (foundSame) {
            return DifferenceStatus.BLUE;
        } else {
            return DifferenceStatus.RED;
        }
    }

}
