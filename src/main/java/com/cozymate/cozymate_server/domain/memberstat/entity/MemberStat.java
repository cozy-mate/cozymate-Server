package com.cozymate.cozymate_server.domain.memberstat.entity;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.university.University;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
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
public class MemberStat extends BaseTimeEntity{

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
    private Map<String, List<String>> options;

    public static MemberStat toMemberStat(
        Member member, University university, Integer admissionYear, MemberStatRequestDTO memberStatRequestDTO) {
        return MemberStat.builder()
            .member(member)
            .university(university)
            .admissionYear(admissionYear)
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
