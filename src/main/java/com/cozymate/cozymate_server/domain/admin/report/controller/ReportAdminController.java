package com.cozymate.cozymate_server.domain.admin.report.controller;

import com.cozymate.cozymate_server.domain.admin.report.dto.ReportAdminResponseDTO;
import com.cozymate.cozymate_server.domain.admin.report.service.ReportAdminService;
import com.cozymate.cozymate_server.global.common.PageDetailResponseDTO;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reports")
public class ReportAdminController {

    private final ReportAdminService reportAdminService;

    @GetMapping
    @Operation(summary = "[무빗] 코지메이트 전체 신고 리스트 조회 (관리자용)", description = "")
    public ResponseEntity<ApiResponse<PageDetailResponseDTO<List<ReportAdminResponseDTO>>>> getReportList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            reportAdminService.getReportList(page, size)
        ));
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "[무빗] 코지메이트 특정 신고 조회 (관리자용)", description = "")
    public ResponseEntity<ApiResponse<ReportAdminResponseDTO>> getReport(
        @PathVariable Long reportId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            reportAdminService.getReportById(reportId)
        ));
    }

    @PatchMapping("/{reportId}")
    @Operation(summary = "[무빗] 영구정지 상태 변경 (관리자용)", description = "사용자가 활동할 수 없도록 영구정지합니다.")
    @SwaggerApiError(
        ErrorStatus._REPORT_NOT_FOUND
    )
    public ResponseEntity<ApiResponse<String>> updateUserBannedStatus(
        @PathVariable Long reportId,
        @RequestParam boolean isBanned
    ) {
        reportAdminService.updateUserBannedStatus(reportId, isBanned);
        return ResponseEntity.ok(
            ApiResponse.onSuccess("변경 완료"));
    }
}
