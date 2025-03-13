package com.cozymate.cozymate_server.domain.report.service;

import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.converter.ReportConverter;
import com.cozymate.cozymate_server.domain.report.dto.request.ReportRequestDTO;
import com.cozymate.cozymate_server.domain.report.repository.ReportRepositoryService;
import com.cozymate.cozymate_server.domain.report.validator.ReportValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportCommandService {

    private final ReportRepositoryService reportRepositoryService;
    private final ReportValidator reportValidator;

    public void saveReport(Member member, ReportRequestDTO requestDTO) {
        reportValidator.checkReportSelf(member, requestDTO.memberId());
        reportValidator.checkReportedMemberExists(requestDTO.memberId());
        reportValidator.checkDuplicateReport(member, requestDTO);

        Report report = ReportConverter.toEntity(member, requestDTO);
        reportRepositoryService.createReport(report);
    }
}