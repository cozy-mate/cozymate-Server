package com.cozymate.cozymate_server.domain.memberstat.memberstat.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum StatKey {

    // --- 도메인 외 필드(그대로 값 사용) ---
    BIRTH_YEAR("birthYear", Type.NOT_LIFESTYLE),
    DORM_JOINING_STATUS("dormJoiningStatus", Type.NOT_LIFESTYLE),
    ADMISSION_YEAR("admissionYear", Type.NOT_LIFESTYLE),
    MAJOR_NAME("majorName", Type.NOT_LIFESTYLE),
    DORM_NAME("dormName", Type.NOT_LIFESTYLE),
    NUM_OF_ROOMMATE("numOfRoommate", Type.NOT_LIFESTYLE),

    // --- 시간 필드(그대로 값 사용) ---
    TURN_OFF_TIME("turnOffTime", Type.TIME),
    SLEEPING_TIME("sleepingTime", Type.TIME),
    WAKE_UP_TIME("wakeUpTime", Type.TIME),

    // --- 단일(인덱스 매핑) ---
    SMOKING_STATUS("smokingStatus", Type.SINGLE),
    COOLING_INTENSITY("coolingIntensity", Type.SINGLE),
    HEATING_INTENSITY("heatingIntensity", Type.SINGLE),
    LIFE_PATTERN("lifePattern", Type.SINGLE),
    INTIMACY("intimacy", Type.SINGLE),
    SHARING_STATUS("sharingStatus", Type.SINGLE),
    GAMING_STATUS("gamingStatus", Type.SINGLE),
    CALLING_STATUS("callingStatus", Type.SINGLE),
    STUDYING_STATUS("studyingStatus", Type.SINGLE),
    EATING_STATUS("eatingStatus", Type.SINGLE),
    NOISE_SENSITIVITY("noiseSensitivity", Type.SINGLE),
    CLEANNESS_SENSITIVITY("cleannessSensitivity", Type.SINGLE),
    CLEANING_FREQUENCY("cleaningFrequency", Type.SINGLE),
    DRINKING_FREQUENCY("drinkingFrequency", Type.SINGLE),
    MBTI("mbti", Type.SINGLE),

    // --- 다중(비트마스크) ---
    PERSONALITIES("personalities", Type.MULTI),
    SLEEPING_HABITS("sleepingHabits", Type.MULTI);
    public enum Type { SINGLE, MULTI, TIME, NOT_LIFESTYLE }

    private final String raw;
    private final Type type;

    StatKey(String raw, Type type) {
        this.raw = raw;
        this.type = type;
    }
    public String raw() { return raw; }
    public Type type() { return type; }

    // raw → enum 매핑
    private static final Map<String, StatKey> BY_RAW = new ConcurrentHashMap<>();
    static {
        for (StatKey k : values()) BY_RAW.put(k.raw, k);
    }
    public static StatKey fromRaw(String raw) { return BY_RAW.get(raw); }
}
