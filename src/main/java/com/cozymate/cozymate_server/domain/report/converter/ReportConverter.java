package com.cozymate.cozymate_server.domain.report.converter;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.dto.request.ReportRequestDTO;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;

public class ReportConverter {

    public static Report toEntity(Member member, ReportRequestDTO requestDto) {
        return Report.builder()
            .reporter(member)
            .reportedMemberId(requestDto.memberId())
            .reportReason(ReportReason.valueOf(requestDto.reason()))
            .reportSource(ReportSource.valueOf(requestDto.source()))
            .content(requestDto.content())
            .build();
    }
}