package com.cozymate.cozymate_server.domain.report.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.converter.ReportConverter;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;
import com.cozymate.cozymate_server.domain.report.repository.ReportRepository;
import com.cozymate.cozymate_server.domain.report.dto.ReportRequestDto;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportCommandService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    public void saveReport(Member member, ReportRequestDto requestDto) {
        checkReportSelf(member, requestDto.getReportedMemberId());
        checkMemberExists(requestDto.getReportedMemberId());
        checkDuplicateReport(member, requestDto);

        Report report = ReportConverter.toEntity(member, requestDto);
        reportRepository.save(report);
    }

    private void checkReportSelf(Member member, Long reportedMemberId) {
        if (member.getId().equals(reportedMemberId)) {
            throw new GeneralException(ErrorStatus._REPORT_CANNOT_REQUEST_SELF);
        }
    }

    private void checkMemberExists(Long reportedMemberId) {
        boolean isExists = memberRepository.existsById(reportedMemberId);

        if (!isExists) {
            throw new GeneralException(ErrorStatus._REPORT_MEMBER_NOT_FOUND);
        }
    }

    private void checkDuplicateReport(Member member, ReportRequestDto requestDto) {
        boolean isAlreadyReported = reportRepository.existsByReporterAndReportedMemberIdAndReportReasonAndReportSource(
            member, requestDto.getReportedMemberId(),
            ReportReason.valueOf(requestDto.getReportReason()),
            ReportSource.valueOf(requestDto.getReportSource()));

        if (isAlreadyReported) {
            throw new GeneralException(ErrorStatus._REPORT_DUPLICATE);
        }
    }
}