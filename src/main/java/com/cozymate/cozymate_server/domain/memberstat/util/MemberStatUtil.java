package com.cozymate.cozymate_server.domain.memberstat.util;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.enums.DifferenceStatus;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class MemberStatUtil {

    // 일관된 순서로 하나의 String을 넣어주기 위해 만든 함수(멤버 스탯의 값이 여러 개 들어갈 수 있을 때 저장용)
    public static String toSortedString(List<String> list) {
        return list.stream()
            .sorted()
            .collect(Collectors.joining(","));
    }

    // 일관된 순서로 정리된 String을 다시 배열로 복구하는 함수
    public static List<String> fromStringToList(String str) {
        return Arrays.stream(str.replaceAll(",$", "").split(","))
            .collect(Collectors.toList());
    }

    // 방에 속한 멤버들의 칩 색깔을 정하기 위해 Stat들을 비교해 Status를 리턴하는 메서드
    public static <T> DifferenceStatus compareField(List<MemberStat> memberStatList,
        BiFunction<Member, MemberStat, T> getter) {

        boolean foundSame = false;
        boolean foundDifferent = false;

        T firstValue = getter.apply(memberStatList.get(0).getMember(), memberStatList.get(0));

        // FIXME: 첫번째 Stat과만 다름 여부를 판단하고 있어, WHITE인 경우도 RED일 때가 있음.
        for (int i = 1; i < memberStatList.size(); i++) {

            T currentValue = getter.apply(memberStatList.get(i).getMember(), memberStatList.get(i));

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

    public static DifferenceStatus compareField(
        Object memberStatMapValue, Object criteriaMemberStatValue){
        if(memberStatMapValue.equals(criteriaMemberStatValue)){
            return DifferenceStatus.BLUE;
        }
        return DifferenceStatus.RED;
    }


    // 아래부터는 특정 멤버스탯을 가져오기 위한 Util들
     public static Map<String, BiFunction<Member,MemberStat,Object>> createFieldGetters() {
        Map<String, BiFunction<Member,MemberStat,Object>> fieldGetters = new HashMap<>();
         fieldGetters.put("admissionYear", (member, memberStat) -> memberStat.getAdmissionYear());
         // 기숙사 정보는 Room 조회시 사용하지 않아 제외
//         fieldGetters.put("numOfRoommate", (member, memberStat) -> memberStat.getNumOfRoommate());
//         fieldGetters.put("dormitoryName", (member, memberStat) -> memberStat.getDormitoryName());
         fieldGetters.put("acceptance", (member, memberStat) -> memberStat.getAcceptance());
         fieldGetters.put("wakeUpTime", (member, memberStat) -> memberStat.getWakeUpTime());
         fieldGetters.put("sleepingTime", (member, memberStat) -> memberStat.getSleepingTime());
         fieldGetters.put("turnOffTime", (member, memberStat) -> memberStat.getTurnOffTime());
         fieldGetters.put("smoking", (member, memberStat) -> memberStat.getSmoking());
         fieldGetters.put("sleepingHabit", (member, memberStat) -> memberStat.getSleepingHabit());
         fieldGetters.put("airConditioningIntensity", (member, memberStat) -> memberStat.getAirConditioningIntensity());
         fieldGetters.put("heatingIntensity", (member, memberStat) -> memberStat.getHeatingIntensity());
         fieldGetters.put("lifePattern", (member, memberStat) -> memberStat.getLifePattern());
         fieldGetters.put("intimacy", (member, memberStat) -> memberStat.getIntimacy());
         fieldGetters.put("canShare", (member, memberStat) -> memberStat.getCanShare());
         fieldGetters.put("isPlayGame", (member, memberStat) -> memberStat.getIsPlayGame());
         fieldGetters.put("isPhoneCall", (member, memberStat) -> memberStat.getIsPhoneCall());
         fieldGetters.put("studying", (member, memberStat) -> memberStat.getStudying());
         fieldGetters.put("intake", (member, memberStat) -> memberStat.getIntake());
         fieldGetters.put("cleanSensitivity", (member, memberStat) -> memberStat.getCleanSensitivity());
         fieldGetters.put("noiseSensitivity", (member, memberStat) -> memberStat.getNoiseSensitivity());
         fieldGetters.put("cleaningFrequency", (member, memberStat) -> memberStat.getCleaningFrequency());
         fieldGetters.put("drinkingFrequency", (member, memberStat) -> memberStat.getDrinkingFrequency());
         fieldGetters.put("personality", (member, memberStat) -> memberStat.getPersonality());
         fieldGetters.put("mbti", (member, memberStat) -> memberStat.getMbti());
         //Member에 있는 항목들
         fieldGetters.put("birthYear", (member, memberStat) -> member.getBirthDay().getYear());
         fieldGetters.put("majorName", (member,memberStat)->member.getMajorName());

        return fieldGetters;
    }

    private static final Map<String, BiFunction<Member,MemberStat,Object>> fieldGetters = createFieldGetters();

    // String으로 필드 value 가져오는 메서드
    public static Object getMemberStatField(MemberStat memberStat, String fieldName) {
        BiFunction<Member,MemberStat,Object> getter = fieldGetters.get(fieldName);
        if (getter == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_PARAMETER_NOT_VALID);
        }
        return getter.apply(memberStat.getMember(),memberStat);
    }

    // 여러 key에 대한 value를 가져오기 위해서 사용하는 메서드
    public static Map<String, Object> getMemberStatFields(MemberStat memberStat, List<String> fieldNames) {
        Map<String, Object> result = new HashMap<>();
        fieldNames.forEach(fieldName ->
            result.put(fieldName, getMemberStatField(memberStat, fieldName))
        );
        return result;
    }

    // 학번 Response 위해 필요한 Util(number->String)
    public static String formatNumber(int number) {
        return String.format("%02d", number);
    }

}
