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
import java.util.function.BiFunction;
import java.util.function.Function;
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

    public static <T> DifferenceStatus compareField(List<MemberStat> memberStatList,
        BiFunction<Member, MemberStat, T> getter) {

        boolean foundSame = false;
        boolean foundDifferent = false;


        T firstValue = getter.apply(memberStatList.get(0).getMember(), memberStatList.get(0));

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


    // 아래부터는 특정 멤버스탯을 가져오기 위한 Util들
     public static Map<String, BiFunction<Member,MemberStat,Object>> createFieldGetters() {
        Map<String, BiFunction<Member,MemberStat,Object>> fieldGetters = new HashMap<>();
         fieldGetters.put("admissionYear", (member, memberStat) -> memberStat.getAdmissionYear());
         fieldGetters.put("numOfRoommate", (member, memberStat) -> memberStat.getNumOfRoommate());
         fieldGetters.put("dormitoryName", (member, memberStat) -> memberStat.getDormitoryName());
         fieldGetters.put("acceptance", (member, memberStat) -> memberStat.getAcceptance());
         fieldGetters.put("wakeUpTime", (member, memberStat) -> memberStat.getWakeUpTime());
         fieldGetters.put("sleepingTime", (member, memberStat) -> memberStat.getSleepingTime());
         fieldGetters.put("turnOffTime", (member, memberStat) -> memberStat.getTurnOffTime());
         fieldGetters.put("smokingState", (member, memberStat) -> memberStat.getSmoking());
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

    public static Object getMemberStatField(MemberStat memberStat, String fieldName) {
        BiFunction<Member,MemberStat,Object> getter = fieldGetters.get(fieldName);
        if (getter == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_PARAMETER_NOT_VALID);
        }
        return getter.apply(memberStat.getMember(),memberStat);
    }

    public static Map<String, Object> getMemberStatFields(MemberStat memberStat, List<String> fieldNames) {
        return fieldNames.stream()
            .collect(Collectors.toMap(
                fieldName -> fieldName, // Map의 키로 사용할 필드 이름
                fieldName -> getMemberStatField(memberStat, fieldName) // Map의 값으로 사용할 필드 값
            ));
    }

    // 학번 Response 위해 필요한 Util
    public static String formatNumber(int number) {
        return String.format("%02d", number);
    }

}
