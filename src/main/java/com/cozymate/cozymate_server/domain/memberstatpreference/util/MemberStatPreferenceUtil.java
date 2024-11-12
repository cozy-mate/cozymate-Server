package com.cozymate.cozymate_server.domain.memberstatpreference.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MemberStatPreferenceUtil {
    private static final Set<String> validPreferences = new HashSet<>(Arrays.asList(
        "birthYear", "admissionYear", "majorName", "acceptance",
        "wakeUpTime", "sleepingTime", "turnOffTime", "smoking",
        "sleepingHabit", "airConditioningIntensity", "heatingIntensity", "lifePattern", "intimacy",
        "canShare", "isPlayGame", "isPhoneCall", "studying", "intake", "cleanSensitivity",
        "noiseSensitivity", "cleaningFrequency", "drinkingFrequency", "personality",
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
