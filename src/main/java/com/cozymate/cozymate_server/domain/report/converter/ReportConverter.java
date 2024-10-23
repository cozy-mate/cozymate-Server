package com.cozymate.cozymate_server.domain.report.converter;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.dto.ReportRequestDto;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;

public class ReportConverter {

    public static Report toEntity(Member member, ReportRequestDto requestDto) {
        return Report.builder()
            .reporter(member)
            .reportedMemberId(requestDto.getReportedMemberId())
            .reportReason(ReportReason.valueOf(requestDto.getReportReason()))
            .reportSource(ReportSource.valueOf(requestDto.getReportSource()))
            .content(requestDto.getContent())
            .build();
    }
}