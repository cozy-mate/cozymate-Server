package com.cozymate.cozymate_server.domain.report.validator;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepository;
import com.cozymate.cozymate_server.domain.report.dto.request.ReportRequestDTO;
import com.cozymate.cozymate_server.domain.report.enums.ReportReason;
import com.cozymate.cozymate_server.domain.report.enums.ReportSource;
import com.cozymate.cozymate_server.domain.report.repository.ReportRepositoryService;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportValidator {

    private final MemberRepository memberRepository;
    private final ReportRepositoryService reportRepositoryService;

    public void checkReportSelf(Member member, Long reportedMemberId) {
        if (member.getId().equals(reportedMemberId)) {
            throw new GeneralException(ErrorStatus._REPORT_CANNOT_REQUEST_SELF);
        }
    }

    public void checkReportedMemberExists(Long reportedMemberId) {
        if (memberRepository.existsById(reportedMemberId)) {
            throw new GeneralException(ErrorStatus._REPORT_MEMBER_NOT_FOUND);
        }
    }

    public void checkDuplicateReport(Member member, ReportRequestDTO requestDTO) {
        boolean isAlreadyReported = reportRepositoryService.existReportByMemberAndTargetMemberAndReasonAndSource(
            member, requestDTO.memberId(), ReportReason.valueOf(requestDTO.reason()),
            ReportSource.valueOf(requestDTO.source()));

        if (isAlreadyReported) {
            throw new GeneralException(ErrorStatus._REPORT_DUPLICATE);
        }
    }
}
