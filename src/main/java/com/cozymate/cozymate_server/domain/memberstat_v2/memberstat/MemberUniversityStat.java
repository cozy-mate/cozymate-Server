package com.cozymate.cozymate_server.domain.memberstat_v2.memberstat;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 대학교에 의존적인 데이터
 * 입학년도(학번), 기숙사명, 기숙사 인원, 합격여부
 * 기숙사 인원 : 학교 마다 인원설정이 어떨지 모르겠음 변경에 열어두기 위해 문자열로 저장
 * - ex. 2인실, 6인3실 등
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
public class MemberUniversityStat {
    private Integer admissionYear;

    private String dormitoryName;

    private String numberOfRoommate;

    private String acceptance;
}
