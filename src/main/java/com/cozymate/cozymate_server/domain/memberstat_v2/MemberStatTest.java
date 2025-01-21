package com.cozymate.cozymate_server.domain.memberstat_v2;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class MemberStatTest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @Embedded
    MemberUniversityStat memberUniversityStat;
    // 학번, 기숙사명, 인실, 합격여부

    @Embedded
    Lifestyle lifestyle;

    String selfIntroduction;

    public void update(
        MemberUniversityStat memberUniversityStat,
        Lifestyle lifestyle,
        String selfIntroduction) {
        this.memberUniversityStat = memberUniversityStat;
        this.lifestyle = lifestyle;
        this.selfIntroduction = selfIntroduction;
    }

}
