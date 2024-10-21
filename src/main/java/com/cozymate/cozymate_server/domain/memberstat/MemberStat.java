package com.cozymate.cozymate_server.domain.memberstat;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO.MemberStatCommandRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.util.MemberStatUtil;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import com.cozymate.cozymate_server.global.utils.TimeUtil;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;

    private Integer admissionYear;

    private String major;

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

    public void update(Member member, University university,
        MemberStatCommandRequestDTO memberStatCommandRequestDTO) {
        this.member = member;
        this.university = university;
        this.acceptance = memberStatCommandRequestDTO.getAcceptance();
        this.admissionYear = Integer.parseInt(memberStatCommandRequestDTO.getAdmissionYear());
        this.major = memberStatCommandRequestDTO.getMajor();
        this.numOfRoommate = memberStatCommandRequestDTO.getNumOfRoommate();
        this.wakeUpTime = TimeUtil.convertTime(memberStatCommandRequestDTO.getWakeUpMeridian(),
            memberStatCommandRequestDTO.getWakeUpTime());
        this.sleepingTime = TimeUtil.convertTime(memberStatCommandRequestDTO.getSleepingMeridian(),
            memberStatCommandRequestDTO.getSleepingTime());
        this.turnOffTime = TimeUtil.convertTime(memberStatCommandRequestDTO.getTurnOffMeridian(),
            memberStatCommandRequestDTO.getTurnOffTime());
        this.smoking = memberStatCommandRequestDTO.getSmokingState();
        this.sleepingHabit = MemberStatUtil.toSortedString(memberStatCommandRequestDTO.getSleepingHabit());
        this.airConditioningIntensity = memberStatCommandRequestDTO.getAirConditioningIntensity();
        this.heatingIntensity = memberStatCommandRequestDTO.getHeatingIntensity();
        this.lifePattern = memberStatCommandRequestDTO.getLifePattern();
        this.intimacy = memberStatCommandRequestDTO.getIntimacy();
        this.canShare = memberStatCommandRequestDTO.getCanShare();
        this.isPlayGame = memberStatCommandRequestDTO.getIsPlayGame();
        this.isPhoneCall = memberStatCommandRequestDTO.getIsPhoneCall();
        this.studying = memberStatCommandRequestDTO.getStudying();
        this.intake = memberStatCommandRequestDTO.getIntake();
        this.cleanSensitivity = memberStatCommandRequestDTO.getCleanSensitivity();
        this.noiseSensitivity = memberStatCommandRequestDTO.getNoiseSensitivity();
        this.cleaningFrequency = memberStatCommandRequestDTO.getCleaningFrequency();
        this.drinkingFrequency = memberStatCommandRequestDTO.getDrinkingFrequency();
        this.personality = MemberStatUtil.toSortedString(memberStatCommandRequestDTO.getPersonality());
        this.mbti = memberStatCommandRequestDTO.getMbti();
        this.selfIntroduction = memberStatCommandRequestDTO.getSelfIntroduction();
    }
}
