package com.cozymate.cozymate_server.domain.memberstat.util;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatDifferenceResponseDTO;
import com.cozymate.cozymate_server.domain.room.enums.DifferenceStatus;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        java.util.function.Function<MemberStat, T> getter) {
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

    // 아래부터는 특정 멤버스탯을 가져오기 위한 Util들
    private static Map<String, Function<MemberStat, Object>> createFieldGetters() {
        Map<String, Function<MemberStat, Object>> fieldGetters = new HashMap<>();
        fieldGetters.put("admissionYear", MemberStat::getAdmissionYear);
        fieldGetters.put("numOfRoommate", MemberStat::getNumOfRoommate);
        fieldGetters.put("acceptance", MemberStat::getAcceptance);
        fieldGetters.put("wakeUpTime", MemberStat::getWakeUpTime);
        fieldGetters.put("sleepingTime", MemberStat::getSleepingTime);
        fieldGetters.put("turnOffTime", MemberStat::getTurnOffTime);
        fieldGetters.put("smokingState", MemberStat::getSmoking);
        fieldGetters.put("sleepingHabit", MemberStat::getSleepingHabit);
        fieldGetters.put("airConditioningIntensity", MemberStat::getAirConditioningIntensity);
        fieldGetters.put("heatingIntensity", MemberStat::getHeatingIntensity);
        fieldGetters.put("lifePattern", MemberStat::getLifePattern);
        fieldGetters.put("intimacy", MemberStat::getIntimacy);
        fieldGetters.put("canShare", MemberStat::getCanShare);
        fieldGetters.put("isPlayGame", MemberStat::getIsPlayGame);
        fieldGetters.put("isPhoneCall", MemberStat::getIsPhoneCall);
        fieldGetters.put("studying", MemberStat::getStudying);
        fieldGetters.put("intake", MemberStat::getIntake);
        fieldGetters.put("cleanSensitivity", MemberStat::getCleanSensitivity);
        fieldGetters.put("noiseSensitivity", MemberStat::getNoiseSensitivity);
        fieldGetters.put("cleaningFrequency", MemberStat::getCleaningFrequency);
        fieldGetters.put("drinkingFrequency", MemberStat::getDrinkingFrequency);
        fieldGetters.put("personality", MemberStat::getPersonality);
        fieldGetters.put("mbti", MemberStat::getMbti);
        return fieldGetters;
    }

    private static final Map<String, Function<MemberStat, Object>> fieldGetters = createFieldGetters();

    private static Object getMemberStatField(MemberStat memberStat, String fieldName) {
        Function<MemberStat, Object> getter = fieldGetters.get(fieldName);
        if (getter == null) {
            throw new GeneralException(ErrorStatus._MEMBERSTAT_PARAMETER_NOT_VALID);
        }
        return getter.apply(memberStat);
    }

    public static List<Object> getMemberStatFields(MemberStat memberStat, List<String> fieldNames) {
        return fieldNames.stream()
            .map(fieldName -> getMemberStatField(memberStat, fieldName))
            .toList();
    }
}
