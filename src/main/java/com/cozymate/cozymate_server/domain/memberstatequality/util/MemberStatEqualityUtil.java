package com.cozymate.cozymate_server.domain.memberstatequality.util;

import com.cozymate.cozymate_server.domain.memberstat.MemberStat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberStatEqualityUtil {

    private static final Integer ADDITIONAL_SCORE = 36;
    private static final Integer NO_SCORE = 0;
    private static final Integer ATTRIBUTE_COUNT = 18;
    private static final Integer HALF_DIVISION = 2;
    private static final Integer QUARTER_DIVISION = 4;
    private static final Integer MULTIPLIER_FOR_PERCENTAGE = 100;
    private static final Integer MAX_SCORE = ADDITIONAL_SCORE * ATTRIBUTE_COUNT;

    // 두 사용자간 일치율을 비교해야 할 때, 사용하는 util
    // 성격은 일치율에서 제외, 출생년도는 보류(총 22개 항목)
    // 제외 목록
//    - 출생년도
//    - 기숙사 합격 여부
//    - 학번
//    - 학과
//    - 성격
//    - mbti
    public static int calculateEquality(MemberStat criteriaMemberStat,
        MemberStat memberStat) {

        int score = NO_SCORE;
        score += criteriaMemberStat.getSmoking().equals(memberStat.getSmoking())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getSleepingHabit().equals(memberStat.getSleepingHabit())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getLifePattern().equals(memberStat.getLifePattern())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getIntimacy().equals(memberStat.getIntimacy())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getCanShare().equals(memberStat.getCanShare())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getIsPlayGame().equals(memberStat.getIsPlayGame())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getIsPhoneCall().equals(memberStat.getIsPhoneCall())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getStudying().equals(memberStat.getStudying())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getIntake().equals(memberStat.getIntake())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getCleaningFrequency().equals(memberStat.getCleaningFrequency())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getAirConditioningIntensity()
            .equals(memberStat.getAirConditioningIntensity())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getHeatingIntensity().equals(memberStat.getHeatingIntensity())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += criteriaMemberStat.getDrinkingFrequency().equals(memberStat.getDrinkingFrequency())
            ? ADDITIONAL_SCORE : NO_SCORE;
        score += calculateTimeScore(criteriaMemberStat.getWakeUpTime(), memberStat.getWakeUpTime());
        score += calculateTimeScore(criteriaMemberStat.getSleepingTime(),
            memberStat.getSleepingTime());
        score += calculateTimeScore(criteriaMemberStat.getTurnOffTime(),
            memberStat.getTurnOffTime());
        score += calculateSensitivityScore(criteriaMemberStat.getCleanSensitivity(),
            memberStat.getCleanSensitivity());
        score += calculateSensitivityScore(criteriaMemberStat.getNoiseSensitivity(),
            memberStat.getNoiseSensitivity());

        double percent = (double) score / MAX_SCORE * MULTIPLIER_FOR_PERCENTAGE;

        return (int) percent;
    }


    // 24시간 반영해 개선함.
    private static int calculateTimeScore(Integer time1, Integer time2) {

        int diff1 = Math.abs(time1 - time2);
        int diff2 = 24 - diff1;

        int timeDifference = Math.min(diff1, diff2);

        return switch (timeDifference) {
            case 0 -> ADDITIONAL_SCORE;
            case 1 -> ADDITIONAL_SCORE / HALF_DIVISION;
            case 2 -> ADDITIONAL_SCORE / QUARTER_DIVISION;
            default -> NO_SCORE;
        };
    }

    private static int calculateSensitivityScore(Integer sensitivity1, Integer sensitivity2) {
        int sensitivityDifference = Math.abs(sensitivity1 - sensitivity2);
        return switch (sensitivityDifference) {
            case 0 -> ADDITIONAL_SCORE;
            case 1 -> ADDITIONAL_SCORE / HALF_DIVISION;
            default -> NO_SCORE;
        };
    }

    // nickname을 {nickname}으로 변경
    public static String getNicknameShowFormat(String nickname) {
        return "{" + nickname + "}";
    }

}
