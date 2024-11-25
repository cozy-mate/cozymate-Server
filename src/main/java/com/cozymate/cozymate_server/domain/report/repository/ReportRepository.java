package com.cozymate.cozymate_server.domain.report.repository;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndReportedMemberIdAndReportReasonAndReportSource(Member reporter,
        Long reportedMemberId, ReportReason reportReason, ReportSource ReportSource);

    List<Report> findAllByReporterId(Long reporterId);

    @Modifying
    @Query("UPDATE Report r SET r.reporter = null WHERE r.reporter = :member")
    void bulkDeleteReporter(@Param("member") Member member);
}