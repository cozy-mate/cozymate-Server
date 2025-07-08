package com.cozymate.cozymate_server.domain.admin.inquiry.controller;

import com.cozymate.cozymate_server.domain.admin.inquiry.dto.InquiryAdminResponseDTO;
import com.cozymate.cozymate_server.domain.admin.inquiry.dto.InquiryAdminReplyRequestDTO;
import com.cozymate.cozymate_server.domain.admin.inquiry.service.InquiryAdminService;
import com.cozymate.cozymate_server.global.common.PageDetailResponseDTO;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/inquiries")
public class InquiryAdminController {

    private final InquiryAdminService inquiryAdminService;

    @GetMapping
    @Operation(summary = "[무빗] 코지메이트 전체 문의 리스트 조회 (관리자용)", description = "")
    public ResponseEntity<ApiResponse<PageDetailResponseDTO<List<InquiryAdminResponseDTO>>>> getInquiryList(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            inquiryAdminService.getInquiryList(page, size)
        ));
    }

    @GetMapping("/{inquiryId}")
    @Operation(summary = "[무빗] 코지메이트 특정 문의 조회 (관리자용)", description = "")
    public ResponseEntity<ApiResponse<InquiryAdminResponseDTO>> getInquiry(
        @PathVariable Long inquiryId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            inquiryAdminService.getInquiryById(inquiryId)
        ));
    }

    @PatchMapping("/{inquiryId}")
    @Operation(summary = "[베로 -> 무빗] 답변 완료로 변경 (관리자용)", description = "근시일 내에 이메일 전송까지 하는 로직 구현 예정(이메일을 보낼지 말지 선택하는 옵션도 추가)")
    @SwaggerApiError(
        ErrorStatus._INQUIRY_NOT_FOUND
    )
    public ResponseEntity<ApiResponse<String>> updateInquiryStatus(
        @PathVariable Long inquiryId,
        @RequestBody InquiryAdminReplyRequestDTO requestDTO
    ) {
        inquiryAdminService.replyInquiry(inquiryId, requestDTO);
        return ResponseEntity.ok(
            ApiResponse.onSuccess("변경 완료"));
    }
}
