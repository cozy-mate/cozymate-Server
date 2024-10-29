package com.cozymate.cozymate_server.domain.report.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.report.service.ReportCommandService;
import com.cozymate.cozymate_server.domain.report.dto.ReportRequestDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportCommandService reportCommandService;

    @PostMapping
    @Operation(
        summary = "[베로] 신고하기",
        description = "reportedMemberId : 신고 대상 사용자 pk\n\n"
            + "ReportSource :  MEMBER_STAT(사용자 상세에서의 신고) , CHAT(쪽지에서의 신고)\n\n"
            + "ReportReason : "
            + "OBSCENITY(음란성/선정성), "
            + "INSULT(욕설/인신공격), "
            + "COMMERCIAL(영리목적/홍보성), "
            + "OTHER(기타)\n\n"
            + "content(신고 내용) : ReportReason이 OTHER(기타 사유)인 경우만 필요"
    )
    @SwaggerApiError({
        ErrorStatus._REPORT_MEMBER_NOT_FOUND,
        ErrorStatus._CANNOT_REPORT_REQUEST_SELF,
        ErrorStatus._REPORT_DUPLICATE
    })
    public ResponseEntity<ApiResponse<String>> saveReport(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody ReportRequestDto reportRequestDto) {
        reportCommandService.saveReport(memberDetails.getMember(), reportRequestDto);
        return ResponseEntity.ok(ApiResponse.onSuccess("신고 완료"));
    }
}