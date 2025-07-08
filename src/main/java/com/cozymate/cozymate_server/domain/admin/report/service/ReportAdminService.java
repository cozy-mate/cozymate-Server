package com.cozymate.cozymate_server.domain.admin.report.service;

import com.cozymate.cozymate_server.domain.admin.report.converter.ReportAdminConverter;
import com.cozymate.cozymate_server.domain.admin.report.dto.ReportAdminResponseDTO;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.member.repository.MemberRepositoryService;
import com.cozymate.cozymate_server.domain.report.Report;
import com.cozymate.cozymate_server.domain.report.repository.ReportRepositoryService;
import com.cozymate.cozymate_server.global.common.PageDetailResponseDTO;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportAdminService {

    private final ReportRepositoryService reportRepositoryService;
    private final MemberRepositoryService memberRepositoryService;

    @Transactional(readOnly = true)
    public PageDetailResponseDTO<List<ReportAdminResponseDTO>> getReportList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Report> reportPage = reportRepositoryService.getAllReportsForAdmin(pageable);

        List<ReportAdminResponseDTO> reportList = reportPage.getContent().stream()
            .map(report -> {
                    Optional<Member> reportedMember = memberRepositoryService.getMemberByIdOptional(
                        report.getReportedMemberId()
                    );
                    String reportedMemberNickname = reportedMember
                        .map(Member::getNickname)
                        .orElse("[탈퇴한 유저]");
                    return ReportAdminConverter.toReportAdminResponseDTO(report,
                        reportedMemberNickname);
                }
            )
            .toList();
        return PageDetailResponseDTO.<List<ReportAdminResponseDTO>>builder()
            .page(page)
            .hasNext(reportPage.hasNext())
            .result(reportList)
            .totalElement(reportPage.getNumberOfElements())
            .totalPage(reportPage.getTotalPages())
            .build();
    }

    @Transactional(readOnly = true)
    public ReportAdminResponseDTO getReportById(Long reportId) {
        Report report = reportRepositoryService.getReportByIdOrThrow(reportId);
        Optional<Member> reportedMember = memberRepositoryService.getMemberByIdOptional(
            report.getReportedMemberId()
        );
        String reportedMemberNickname = reportedMember
            .map(Member::getNickname)
            .orElse("[탈퇴한 유저]");
        return ReportAdminConverter.toReportAdminResponseDTO(report, reportedMemberNickname);
    }

    @Transactional
    public void updateUserBannedStatus(Long reportId, boolean isBanned) {
        Report report = reportRepositoryService.getReportByIdOrThrow(reportId);
        Member reportedMember = memberRepositoryService.getMemberByIdOrThrow(
            report.getReportedMemberId()
        );
        reportedMember.updateBannedStatus(isBanned);
    }
}
