package com.cozymate.cozymate_server.domain.memberstat_v2;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * 코지메이트 질문에 대한 답변 코지메이트 기획상으로 통제 가능하기에 모두 정수
 */
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Lifestyle {

    @Range(min = 0, max = 23)
    private Integer wakeUpTime;

    @Range(min = 0, max = 23)
    private Integer sleepingTime;

    @Range(min = 0, max = 23)
    private Integer turnOffTime;

    @Range(min = 0, max = 3)
    private Integer smokingStatus;

    @Range(min = 0, max = 31)
    private Integer sleepingHabit;
    /**
     * 중복 선택 가능
     *     0 : 잠버릇 없음
     *     코골이, 이갈이, 몽유병, 잠꼬대, 뒤척임 비트별로 관리
     */

    @Range(min = 0, max = 3)
    private Integer coolingIntensity;

    @Range(min = 0, max = 3)
    private Integer heatingIntensity;

    @Range(min = 0, max = 1)
    private Integer lifePattern;

    @Range(min = 0, max = 2)
    private Integer intimacy;

    @Range(min = 0, max = 3)
    private Integer itemSharing;

    @Range(min = 0, max = 2)
    private Integer playingGameFrequency;

    @Range(min = 0, max = 2)
    private Integer phoneCallingFrequency;

    @Range(min = 0, max = 2)
    private Integer studyingFrequency;

    @Range(min = 0, max = 3)
    private Integer eatingFrequency;

    @Range(min = 0, max = 4)
    private Integer cleannessSensitivity;

    @Range(min = 0, max = 4)
    private Integer noiseSensitivity;

    @Range(min = 0, max = 4)
    private Integer cleaningFrequency;

    @Range(min = 0, max = 4)
    private Integer drinkingFrequency;

    @Range(min = 1, max = 4095)
    private Integer personality;
    /**
     * 중복 선택 가능
     *     선택지 12개 0b 0000 0000 0001 ~ 0b 1111 1111 1111
     */

    @Range(min = 0, max = 15)
    private Integer mbti;
}
