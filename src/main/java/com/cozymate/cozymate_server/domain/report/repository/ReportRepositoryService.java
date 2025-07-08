package com.cozymate.cozymate_server.domain.report.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Report> getAllReportsForAdmin(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    public Report getReportByIdOrThrow(Long reportId) {
        return reportRepository.findById(reportId)
            .orElseThrow(() -> new GeneralException(ErrorStatus._REPORT_NOT_FOUND));
    }
}
