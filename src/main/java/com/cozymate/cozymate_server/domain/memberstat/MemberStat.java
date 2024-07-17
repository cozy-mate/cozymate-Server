package com.cozymate.cozymate_server.domain.memberstat;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.enums.Acceptance;
import com.cozymate.cozymate_server.domain.memberstat.enums.SmokingState;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;


@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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

}
