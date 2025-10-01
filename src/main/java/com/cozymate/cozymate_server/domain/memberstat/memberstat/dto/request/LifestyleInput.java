package com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.request;


import java.util.List;

public interface LifestyleInput {
    Integer wakeUpTime();
    Integer sleepingTime();
    Integer turnOffTime();
    String  smokingStatus();
    List<String> sleepingHabits();
    String  coolingIntensity();
    String  heatingIntensity();
    String  lifePattern();
    String  intimacy();
    String  sharingStatus();
    String  gamingStatus();
    String  callingStatus();
    String  studyingStatus();
    String  eatingStatus();
    String  noiseSensitivity();
    String  cleannessSensitivity();
    String  cleaningFrequency();
    String  drinkingFrequency();
    List<String> personalities();
    String  mbti();
}
