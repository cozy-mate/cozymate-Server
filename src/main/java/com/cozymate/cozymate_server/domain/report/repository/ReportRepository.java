package com.cozymate.cozymate_server.domain.report.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndReportedMemberIdAndReportReasonAndReportSource(Member reporter,
        Long reportedMemberId, ReportReason reportReason, ReportSource ReportSource);
}