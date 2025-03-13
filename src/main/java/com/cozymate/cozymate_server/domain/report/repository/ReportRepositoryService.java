package com.cozymate.cozymate_server.domain.report.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportRepositoryService {

    private final ReportRepository reportRepository;

    public boolean existReportByMemberAndTargetMemberAndReasonAndSource(Member member,
        Long targetMemberId, ReportReason reportReason, ReportSource reportSource) {
        return reportRepository.existsByReporterAndReportedMemberIdAndReportReasonAndReportSource(
            member, targetMemberId, reportReason, reportSource);
    }

    public void createReport(Report report) {
        reportRepository.save(report);
    }
}
