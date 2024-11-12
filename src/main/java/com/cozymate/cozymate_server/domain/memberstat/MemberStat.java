package com.cozymate.cozymate_server.domain.memberstat;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import com.cozymate.cozymate_server.global.utils.TimeUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity
public class MemberStat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    private Integer admissionYear;

    private String dormitoryName;

    private Integer numOfRoommate;

    private String acceptance;

    private Integer wakeUpTime;

    private Integer sleepingTime;

    private Integer turnOffTime;

    private String smoking;

    private String sleepingHabit;

    private Integer airConditioningIntensity;

    private Integer heatingIntensity;

    private String lifePattern;

    private String intimacy;

    private String canShare;

    private String isPlayGame;

    private String isPhoneCall;

    private String studying;

    private String intake;

    private Integer cleanSensitivity;

    private Integer noiseSensitivity;

    private String cleaningFrequency;

    private String drinkingFrequency;

    private String personality;

    private String mbti;

    private String selfIntroduction;

    public void update(Member member,
        CreateMemberStatRequestDTO createMemberStatRequestDTO) {
        this.member = member;
        this.acceptance = createMemberStatRequestDTO.acceptance();
        this.admissionYear = Integer.parseInt(createMemberStatRequestDTO.admissionYear());
        this.dormitoryName = createMemberStatRequestDTO.dormitoryName();
        this.numOfRoommate = createMemberStatRequestDTO.numOfRoommate();
        this.wakeUpTime = TimeUtil.convertTime(createMemberStatRequestDTO.wakeUpMeridian(),
            createMemberStatRequestDTO.wakeUpTime());
        this.sleepingTime = TimeUtil.convertTime(createMemberStatRequestDTO.sleepingMeridian(),
            createMemberStatRequestDTO.sleepingTime());
        this.turnOffTime = TimeUtil.convertTime(createMemberStatRequestDTO.turnOffMeridian(),
            createMemberStatRequestDTO.turnOffTime());
        this.smoking = createMemberStatRequestDTO.smokingState();
        this.sleepingHabit = MemberStatUtil.toSortedString(createMemberStatRequestDTO.sleepingHabit());
        this.airConditioningIntensity = createMemberStatRequestDTO.airConditioningIntensity();
        this.heatingIntensity = createMemberStatRequestDTO.heatingIntensity();
        this.lifePattern = createMemberStatRequestDTO.lifePattern();
        this.intimacy = createMemberStatRequestDTO.intimacy();
        this.canShare = createMemberStatRequestDTO.canShare();
        this.isPlayGame = createMemberStatRequestDTO.isPlayGame();
        this.isPhoneCall = createMemberStatRequestDTO.isPhoneCall();
        this.studying = createMemberStatRequestDTO.studying();
        this.intake = createMemberStatRequestDTO.intake();
        this.cleanSensitivity = createMemberStatRequestDTO.cleanSensitivity();
        this.noiseSensitivity = createMemberStatRequestDTO.noiseSensitivity();
        this.cleaningFrequency = createMemberStatRequestDTO.cleaningFrequency();
        this.drinkingFrequency = createMemberStatRequestDTO.drinkingFrequency();
        this.personality = MemberStatUtil.toSortedString(createMemberStatRequestDTO.personality());
        this.mbti = createMemberStatRequestDTO.mbti();
        this.selfIntroduction = createMemberStatRequestDTO.selfIntroduction();
    }
}
