package com.cozymate.cozymate_server.domain.report.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.converter.ReportConverter;
import com.cozymate.cozymate_server.domain.report.dto.request.ReportRequestDTO;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;
import com.cozymate.cozymate_server.domain.report.repository.ReportRepository;
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

    public void saveReport(Member member, ReportRequestDTO requestDTO) {
        checkReportSelf(member, requestDTO.memberId());
        checkMemberExists(requestDTO.memberId());
        checkDuplicateReport(member, requestDTO);

        Report report = ReportConverter.toEntity(member, requestDTO);
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

    private void checkDuplicateReport(Member member, ReportRequestDTO requestDTO) {
        boolean isAlreadyReported = reportRepository.existsByReporterAndReportedMemberIdAndReportReasonAndReportSource(
            member, requestDTO.memberId(),
            ReportReason.valueOf(requestDTO.reason()),
            ReportSource.valueOf(requestDTO.source()));

        if (isAlreadyReported) {
            throw new GeneralException(ErrorStatus._REPORT_DUPLICATE);
        }
    }
}