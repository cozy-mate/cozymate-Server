package com.cozymate.cozymate_server.domain.memberstat_v2;

import com.cozymate.cozymate_server.global.utils.BaseTimeEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    Long memberId;

    @Embedded
    MemberUniversityStat memberUniversityStat;
    // 학번, 기숙사명, 인실, 합격여부

    @Embedded
    Lifestyle lifestyle;

    String selfIntroduction;

}
