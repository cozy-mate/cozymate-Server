package com.cozymate.cozymate_server.domain.memberstat.lifestylematchrate.util;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.Lifestyle;
import java.util.ArrayList;
import java.util.List;

public class MemberMatchRateCalculator {

    private static final double ADDITIONAL_SCORE = 12.0;
    private static final double NO_SCORE = 0.0;
    private static final double HALF_DIVISION = 2.0;
    private static final double QUARTER_DIVISION = 4.0;
    private static final double MULTIPLIER_FOR_PERCENTAGE = 100.0;
    private static final int NUMBER_OF_SLEEPING_HABIT_ANSWERS = 6;

    public static int calculateLifestyleMatchRate(Lifestyle lifestyle1, Lifestyle lifestyle2) {
        List<Double> matchScores = new ArrayList<>();

        matchScores.add(calculateTimeScore(lifestyle1.getWakeUpTime(), lifestyle2.getWakeUpTime()));
        matchScores.add(
            calculateTimeScore(lifestyle1.getSleepingTime(), lifestyle2.getSleepingTime()));
        matchScores.add(
            calculateTimeScore(lifestyle1.getTurnOffTime(), lifestyle2.getTurnOffTime()));
        matchScores.add(
            calculateEquality(lifestyle1.getSmokingStatus(), lifestyle2.getSmokingStatus()));
        matchScores.add(calculateDuplicationSelection(lifestyle1.getSleepingHabit(),
            lifestyle2.getSleepingHabit(), NUMBER_OF_SLEEPING_HABIT_ANSWERS));
        matchScores.add(
            calculateEquality(lifestyle1.getCoolingIntensity(), lifestyle2.getCoolingIntensity()));
        matchScores.add(
            calculateEquality(lifestyle1.getHeatingIntensity(), lifestyle2.getHeatingIntensity()));
        matchScores.add(
            calculateEquality(lifestyle1.getLifePattern(), lifestyle2.getLifePattern()));
        matchScores.add(calculateEquality(lifestyle1.getIntimacy(), lifestyle2.getIntimacy()));
        matchScores.add(
            calculateEquality(lifestyle1.getItemSharing(), lifestyle2.getItemSharing()));
        matchScores.add(calculateEquality(lifestyle1.getPlayingGameFrequency(),
            lifestyle2.getPlayingGameFrequency()));
        matchScores.add(calculateEquality(lifestyle1.getPhoneCallingFrequency(),
            lifestyle2.getPhoneCallingFrequency()));
        matchScores.add(calculateEquality(lifestyle1.getStudyingFrequency(),
            lifestyle2.getStudyingFrequency()));
        matchScores.add(
            calculateEquality(lifestyle1.getEatingFrequency(), lifestyle2.getEatingFrequency()));
        matchScores.add(calculateSensitivityScore(lifestyle1.getCleannessSensitivity(),
            lifestyle2.getCleannessSensitivity()));
        matchScores.add(calculateSensitivityScore(lifestyle1.getNoiseSensitivity(),
            lifestyle2.getNoiseSensitivity()));
        matchScores.add(calculateEquality(lifestyle1.getCleaningFrequency(),
            lifestyle2.getCleaningFrequency()));
        matchScores.add(calculateEquality(lifestyle1.getDrinkingFrequency(),
            lifestyle2.getDrinkingFrequency()));

        // 동적으로 최대 점수 계산
        double maxScore = matchScores.size() * ADDITIONAL_SCORE;
        double totalScore = matchScores.stream().mapToDouble(Double::doubleValue).sum();

        // 100점 만점 정규화 후 정수로 변환
        return (int) ((totalScore / maxScore) * MULTIPLIER_FOR_PERCENTAGE);
    }

    private static double calculateEquality(Integer value1, Integer value2) {
        return value1.equals(value2) ? ADDITIONAL_SCORE : NO_SCORE;
    }

    private static double calculateTimeScore(Integer time1, Integer time2) {
        int timeDifference = Math.min((time2 - time1 + 24) % 24, (time1 - time2 + 24) % 24);
        if (timeDifference == 0) {
            return ADDITIONAL_SCORE;
        }
        if (timeDifference == 1) {
            return ADDITIONAL_SCORE / HALF_DIVISION;
        }
        if (timeDifference == 2) {
            return ADDITIONAL_SCORE / QUARTER_DIVISION;
        }
        return NO_SCORE;
    }

    private static double calculateSensitivityScore(Integer sensitivity1, Integer sensitivity2) {
        int sensitivityDifference = Math.abs(sensitivity1 - sensitivity2);
        if (sensitivityDifference == 0) {
            return ADDITIONAL_SCORE;
        }
        if (sensitivityDifference == 1) {
            return ADDITIONAL_SCORE / HALF_DIVISION;
        }
        return NO_SCORE;
    }

    private static double calculateDuplicationSelection(Integer value1, Integer value2,
        double numberOfSelection) {
        int matchingBits = ~(value1 ^ value2);
        int count = 0;
        for (int i = 0; i < numberOfSelection; i++) {
            count += (matchingBits >> i) & 1;
        }

        return (count / numberOfSelection) * ADDITIONAL_SCORE;
    }
}

