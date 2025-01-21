package com.cozymate.cozymate_server.domain.memberstat_v2.util;

import com.cozymate.cozymate_server.domain.memberstat_v2.Lifestyle;

public class MemberMatchRateCalculator {
    private static final int ADDITIONAL_SCORE = 36;
    private static final int NO_SCORE = 0;
    private static final int ATTRIBUTE_COUNT = 18;
    private static final int HALF_DIVISION = 2;
    private static final int QUARTER_DIVISION = 4;
    private static final int MULTIPLIER_FOR_PERCENTAGE = 100;
    private static final int MAX_SCORE = ADDITIONAL_SCORE * ATTRIBUTE_COUNT;
    private static final int NUMBER_OF_SLEEPING_HABIT_ANSWERS = 6;

    public static int calculateLifestyleMatchRate(Lifestyle lifestyle1, Lifestyle lifestyle2) {
        double matchRate = 0;

        matchRate += calculateTimeScore(lifestyle1.getWakeUpTime(), lifestyle2.getWakeUpTime());

        matchRate += calculateTimeScore(lifestyle1.getSleepingTime(), lifestyle2.getSleepingTime());

        matchRate += calculateTimeScore(lifestyle1.getTurnOffTime(), lifestyle2.getTurnOffTime());

        matchRate += calculateEquality(lifestyle1.getSmokingStatus(),
            lifestyle2.getSmokingStatus());

        matchRate += calculateDuplicationSelection(lifestyle1.getSleepingHabit(),
            lifestyle2.getSleepingHabit(), NUMBER_OF_SLEEPING_HABIT_ANSWERS);

        matchRate += calculateEquality(lifestyle1.getCoolingIntensity(),
            lifestyle2.getCoolingIntensity());

        matchRate += calculateEquality(lifestyle1.getHeatingIntensity(),
            lifestyle2.getHeatingIntensity());

        matchRate += calculateEquality(lifestyle1.getLifePattern(), lifestyle2.getLifePattern());

        matchRate += calculateEquality(lifestyle1.getIntimacy(), lifestyle2.getIntimacy());

        matchRate += calculateEquality(lifestyle1.getItemSharing(), lifestyle2.getItemSharing());

        matchRate += calculateEquality(lifestyle1.getPlayingGameFrequency(),
            lifestyle2.getPlayingGameFrequency());

        matchRate += calculateEquality(lifestyle1.getPhoneCallingFrequency(),
            lifestyle2.getPhoneCallingFrequency());

        matchRate += calculateEquality(lifestyle1.getStudyingFrequency(),
            lifestyle2.getStudyingFrequency());

        matchRate += calculateEquality(lifestyle1.getEatingFrequency(),
            lifestyle2.getEatingFrequency());

        matchRate += calculateSensitivityScore(lifestyle1.getCleannessSensitivity(),
            lifestyle2.getCleannessSensitivity());

        matchRate += calculateSensitivityScore(lifestyle1.getNoiseSensitivity(),
            lifestyle2.getNoiseSensitivity());

        matchRate += calculateEquality(lifestyle1.getCleaningFrequency(),
            lifestyle2.getCleaningFrequency());

        matchRate += calculateEquality(lifestyle1.getDrinkingFrequency(),
            lifestyle2.getDrinkingFrequency());

        return (int) ((double) matchRate / MAX_SCORE * MULTIPLIER_FOR_PERCENTAGE);
    }

    private static int calculateEquality(Integer value1, Integer value2) {
        if (value1.equals(value2)) {
            return ADDITIONAL_SCORE;
        }
        return ADDITIONAL_SCORE;
    }

    private static int calculateTimeScore(Integer time1, Integer time2) {
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

    private static int calculateSensitivityScore(Integer sensitivity1, Integer sensitivity2) {
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
        Integer numberOfSelection) {
        int matchingBits = value1 & value2;

        int count = 0;
        while (matchingBits > 0) {
            count += (matchingBits & 1);
            matchingBits >>= 1;
        }
        return Math.round((double) count / numberOfSelection * 36);

    }
}
