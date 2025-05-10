package com.cozymate.cozymate_server.domain.memberstat.memberstat.redis.util;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberStat;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.MemberUniversityStat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MemberStatExtractor {

    private MemberStatExtractor() {
    }

    private static final Set<String> MULTI_VALUE_QUESTION = Set.of("personalities", "sleepingHabits");

    public static Map<String, List<?>> toFilterMap(MemberStat memberStat, List<String> filterList) {
        Map<String, String> allAnswers = extractAnswers(memberStat);

        return filterList.stream()
            .filter(allAnswers::containsKey)
            .collect(Collectors.toMap(
                key -> key,
                key -> {
                    String raw = allAnswers.get(key);
                    if (MULTI_VALUE_QUESTION.contains(key)) {
                        return Arrays.stream(raw.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                    }
                    return List.of(raw);
                }
            ));
    }
    public static Map<String, String> extractAnswers(MemberStat memberStat) {
        Map<String, String> answers = new HashMap<>();

        Lifestyle lifestyle = memberStat.getLifestyle();
        MemberUniversityStat universityStat = memberStat.getMemberUniversityStat();

        // UniversityStat
        answers.put("admissionYear", toStringOrEmpty(universityStat.getAdmissionYear()));
        answers.put("dormitoryName", toStringOrEmpty(universityStat.getDormitoryName()));
        answers.put("numberOfRoommate", toStringOrEmpty(universityStat.getNumberOfRoommate()));
        answers.put("acceptance", toStringOrEmpty(universityStat.getAcceptance()));

        // Lifestyle 단일 선택 항목
        answers.put("wakeUpTime", toStringOrEmpty(lifestyle.getWakeUpTime()));
        answers.put("sleepingTime", toStringOrEmpty(lifestyle.getSleepingTime()));
        answers.put("turnOffTime", toStringOrEmpty(lifestyle.getTurnOffTime()));
        answers.put("smokingStatus", toStringOrEmpty(lifestyle.getSmokingStatus()));
        answers.put("coolingIntensity", toStringOrEmpty(lifestyle.getCoolingIntensity()));
        answers.put("heatingIntensity", toStringOrEmpty(lifestyle.getHeatingIntensity()));
        answers.put("lifePattern", toStringOrEmpty(lifestyle.getLifePattern()));
        answers.put("intimacy", toStringOrEmpty(lifestyle.getIntimacy()));
        answers.put("sharingStatus", toStringOrEmpty(lifestyle.getItemSharing()));
        answers.put("gamingStatus", toStringOrEmpty(lifestyle.getPlayingGameFrequency()));
        answers.put("callingStatus", toStringOrEmpty(lifestyle.getPhoneCallingFrequency()));
        answers.put("studyingStatus", toStringOrEmpty(lifestyle.getStudyingFrequency()));
        answers.put("eatingStatus", toStringOrEmpty(lifestyle.getEatingFrequency()));
        answers.put("cleannessSensitivity", toStringOrEmpty(lifestyle.getCleannessSensitivity()));
        answers.put("noiseSensitivity", toStringOrEmpty(lifestyle.getNoiseSensitivity()));
        answers.put("cleaningFrequency", toStringOrEmpty(lifestyle.getCleaningFrequency()));
        answers.put("drinkingFrequency", toStringOrEmpty(lifestyle.getDrinkingFrequency()));
        answers.put("mbti", toStringOrEmpty(lifestyle.getMbti()));

        // 비트마스크: sleepingHabit (6개)
        StringBuilder sleepingHabitKeys = new StringBuilder();
        int sleepingHabit = lifestyle.getSleepingHabit();
        for (int i = 0; i < 6; i++) {
            if ((sleepingHabit & (1 << i)) != 0) {
                sleepingHabitKeys.append(i).append(",");
            }
        }
        answers.put("sleepingHabits", sleepingHabitKeys.toString());

        // 비트마스크: personality (12개)
        StringBuilder personalityKeys = new StringBuilder();
        int personality = lifestyle.getPersonality();
        for (int i = 0; i < 12; i++) {
            if ((personality & (1 << i)) != 0) {
                personalityKeys.append(i).append(",");
            }
        }
        answers.put("personalities", personalityKeys.toString());

        return answers;
    }

    private static String toStringOrEmpty(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    private static String toBitString(int value, int bitLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bitLength; i++) {
            sb.append((value & (1 << i)) != 0 ? '1' : '0');
        }
        return sb.reverse().toString(); // LSB → MSB로 정렬
    }
}
