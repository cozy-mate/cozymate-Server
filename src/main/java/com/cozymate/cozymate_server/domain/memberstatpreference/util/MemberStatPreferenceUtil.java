package com.cozymate.cozymate_server.domain.memberstatpreference.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MemberStatPreferenceUtil {
    private static final Set<String> validPreferences = new HashSet<>(Arrays.asList(
        "birthYear", "admissionYear", "majorName", "dormJoiningStatus",
        "wakeUpTime", "sleepingTime", "turnOffTime", "smokingStatus",
        "sleepingHabits", "coolingIntensity", "heatingIntensity", "lifePattern",
        "intimacy", "sharingStatus", "gamingStatus", "callingStatus",
        "studyingStatus", "eatingStatus", "cleannessSensitivity",
        "noiseSensitivity", "cleaningFrequency", "drinkingFrequency", "personalities",
        "mbti"
    ));

    public static boolean areValidPreferences(List<String> preferences) {
        for (String preference : preferences) {
            if (!validPreferences.contains(preference)) {
                return false;
            }
        }
        return true;
    }
}
