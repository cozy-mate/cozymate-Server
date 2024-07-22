package com.cozymate.cozymate_server.domain.memberstat;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.enums.Acceptance;
import com.cozymate.cozymate_server.domain.memberstat.enums.SmokingState;
import com.cozymate.cozymate_server.domain.university.University;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;


@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class MemberStat extends Member {

    private Integer admissionYear;

    private String major;

    private Integer numOfRoommate;

    private Acceptance acceptance;

    private Integer wakeUpTime;

    private Integer sleepingTime;

    private Integer turnOffTime;

    private SmokingState smoking;

    private String sleepingHabit;

    private String constitution;

    private String lifePattern;

    private String intimacy;

    private Boolean canShare;

    private Boolean isPlayGame;

    private Boolean isPhoneCall;

    private String studying;

    private Integer cleanSensitivity;

    private Integer noiseSensitivity;

    private String cleaningFrequency;

    private String personality;

    private String mbti;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, List<Long>> options;

    @Builder(builderMethodName = "memberStatBuilder")
    public MemberStat(Long id, University university, String clientId, String name, String nickname, Gender gender, LocalDate birthDay, Integer persona,
        Integer admissionYear, String major, Integer numOfRoommate, Acceptance acceptance, Integer wakeUpTime, Integer sleepingTime,
        Integer turnOffTime, SmokingState smoking, String sleepingHabit, String constitution, String lifePattern, String intimacy,
        Boolean canShare, Boolean isPlayGame, Boolean isPhoneCall, String studying, Integer cleanSensitivity, Integer noiseSensitivity,
        String cleaningFrequency, String personality, String mbti, Map<String, List<Long>> options) {
        super(id, university, clientId, name, nickname, gender, birthDay, persona);
        this.admissionYear = admissionYear;
        this.major = major;
        this.numOfRoommate = numOfRoommate;
        this.acceptance = acceptance;
        this.wakeUpTime = wakeUpTime;
        this.sleepingTime = sleepingTime;
        this.turnOffTime = turnOffTime;
        this.smoking = smoking;
        this.sleepingHabit = sleepingHabit;
        this.constitution = constitution;
        this.lifePattern = lifePattern;
        this.intimacy = intimacy;
        this.canShare = canShare;
        this.isPlayGame = isPlayGame;
        this.isPhoneCall = isPhoneCall;
        this.studying = studying;
        this.cleanSensitivity = cleanSensitivity;
        this.noiseSensitivity = noiseSensitivity;
        this.cleaningFrequency = cleaningFrequency;
        this.personality = personality;
        this.mbti = mbti;
        this.options = options;
    }

    public static MemberStat of(Member member, MemberStatRequestDTO memberStatRequestDTO) {
        return MemberStat.memberStatBuilder()
            .id(member.getId())
            .university(member.getUniversity())
            .clientId(member.getClientId())
            .name(member.getName())
            .nickname(member.getNickname())
            .gender(member.getGender())
            .birthDay(member.getBirthDay())
            .persona(member.getPersona())
            .admissionYear(memberStatRequestDTO.getAdmissionYear())
            .major(memberStatRequestDTO.getMajor())
            .numOfRoommate(memberStatRequestDTO.getNumOfRoommate())
            .acceptance(memberStatRequestDTO.getAcceptance())
            .wakeUpTime(memberStatRequestDTO.getWakeUpTime())
            .sleepingTime(memberStatRequestDTO.getSleepingTime())
            .turnOffTime(memberStatRequestDTO.getTurnOffTime())
            .smoking(memberStatRequestDTO.getSmokingState())
            .sleepingHabit(memberStatRequestDTO.getSleepingHabit())
            .constitution(memberStatRequestDTO.getConstitution())
            .lifePattern(memberStatRequestDTO.getLifePattern())
            .intimacy(memberStatRequestDTO.getIntimacy())
            .canShare(memberStatRequestDTO.getCanShare())
            .isPlayGame(memberStatRequestDTO.getIsPlayGame())
            .isPhoneCall(memberStatRequestDTO.getIsPhoneCall())
            .studying(memberStatRequestDTO.getStudying())
            .cleanSensitivity(memberStatRequestDTO.getCleanSensitivity())
            .noiseSensitivity(memberStatRequestDTO.getNoiseSensitivity())
            .cleaningFrequency(memberStatRequestDTO.getCleaningFrequency())
            .personality(memberStatRequestDTO.getPersonality())
            .mbti(memberStatRequestDTO.getMbti())
            .options(memberStatRequestDTO.getOptions())
            .build();
    }
}
