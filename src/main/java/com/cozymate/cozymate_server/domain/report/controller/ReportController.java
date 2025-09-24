package com.cozymate.cozymate_server.domain.report.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.report.dto.request.ReportRequestDTO;
import com.cozymate.cozymate_server.domain.report.service.ReportCommandService;
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
        description = "memberId : 신고 대상 사용자 pk\n\n"
            + "source :  MEMBER_STAT(사용자 상세에서의 신고) , MESSAGE(쪽지에서의 신고)\n\n"
            + "reason : "
            + "OBSCENITY(음란성/선정성), "
            + "INSULT(욕설/인신공격), "
            + "COMMERCIAL(영리목적/홍보성), "
            + "OTHER(기타)\n\n"
            + "content(신고 내용) : reason이 OTHER(기타 사유)인 경우만 필요"
    )
    @SwaggerApiError({
        ErrorStatus._REPORT_MEMBER_NOT_FOUND,
        ErrorStatus._REPORT_CANNOT_REQUEST_SELF,
        ErrorStatus._REPORT_DUPLICATE
    })
    public ResponseEntity<ApiResponse<String>> saveReport(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody ReportRequestDTO reportRequestDTO) {
        reportCommandService.saveReport(memberDetails.member(), reportRequestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess("신고 완료"));
    }
}