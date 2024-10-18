package com.cozymate.cozymate_server.domain.memberstat.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MemberStatUtil {

    public static String toSortedString(List<String> list) {
        return list.stream()
            .sorted()
            .collect(Collectors.joining(",")) + ",";
    }

    public static List<String> fromStringToList(String str) {
        return Arrays.stream(str.replaceAll(",$", "").split(","))
            .collect(Collectors.toList());
    }
}
