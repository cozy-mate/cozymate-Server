package com.cozymate.cozymate_server.domain.admin.report.converter;

import com.cozymate.cozymate_server.domain.admin.report.dto.ReportAdminResponseDTO;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.Report;
import java.util.Optional;

public class ReportAdminConverter {

    public static ReportAdminResponseDTO toReportAdminResponseDTO(Report report,
        String reportedNickname) {
        return ReportAdminResponseDTO.builder()
            .reportId(report.getId())
            .reporterMemberId(Optional.ofNullable(report.getReporter())
                .map(Member::getId)
                .orElse(null))
            .reporterNickname(Optional.ofNullable(report.getReporter())
                .map(Member::getNickname)
                .orElse("[탈퇴한 유저]"))
            .reportedMemberId(report.getReportedMemberId())
            .reportedNickname(reportedNickname)
            .reportReason(report.getReportReason().toString())
            .reportSource(report.getReportSource().toString())
            .content(report.getContent())
            .createdAt(report.getCreatedAt())
            .build();
    }

}
