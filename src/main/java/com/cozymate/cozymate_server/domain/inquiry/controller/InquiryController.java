package com.cozymate.cozymate_server.domain.inquiry.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.inquiry.dto.request.CreateInquiryRequestDTO;
import com.cozymate.cozymate_server.domain.inquiry.dto.response.InquiryDetailResponseDTO;
import com.cozymate.cozymate_server.domain.inquiry.service.InquiryCommandService;
import com.cozymate.cozymate_server.domain.inquiry.service.InquiryQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
public class InquiryController {

    private final InquiryCommandService inquiryCommandService;
    private final InquiryQueryService inquiryQueryService;

    @PostMapping
    @Operation(summary = "[베로] 문의하기", description = "body에 content: 문의 내용, email: 이메일")
    @SwaggerApiError(
        ErrorStatus._INQUIRY_EMAIL_FORMAT_INVALID
    )
    public ResponseEntity<ApiResponse<String>> createInquiry(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody CreateInquiryRequestDTO createInquiryRequestDTO) {
        inquiryCommandService.createInquiry(memberDetails.member(), createInquiryRequestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess("문의 완료"));
    }

    @GetMapping
    @Operation(summary = "[베로] 문의 내역 목록 조회", description = "")
    public ResponseEntity<ApiResponse<List<InquiryDetailResponseDTO>>> getInquiryList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(inquiryQueryService.getInquiryList(memberDetails.member())));
    }

    @GetMapping("/exist")
    @Operation(summary = "[베로] 문의 내역 존재 여부 조회", description = "true/유, false/무")
    public ResponseEntity<ApiResponse<Boolean>> existInquiryRecord(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(inquiryQueryService.getInquiryRecord(memberDetails.member())));
    }
}