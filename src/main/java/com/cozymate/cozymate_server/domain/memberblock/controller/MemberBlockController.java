package com.cozymate.cozymate_server.domain.memberblock.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.memberblock.dto.MemberBlockRequestDto;
import com.cozymate.cozymate_server.domain.memberblock.dto.MemberBlockResponseDto;
import com.cozymate.cozymate_server.domain.memberblock.service.MemberBlockCommandService;
import com.cozymate.cozymate_server.domain.memberblock.service.MemberBlockQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/block/member")
public class MemberBlockController {

    private final MemberBlockCommandService memberBlockCommandService;
    private final MemberBlockQueryService memberBlockQueryService;

    @PostMapping
    @Operation(summary = "[베로] 멤버 차단", description = "body에 차단할 멤버 id")
    @SwaggerApiError({
        ErrorStatus._CANNOT_BLOCK_REQUEST_SELF,
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ALREADY_BLOCKED_MEMBER
    })
    public ResponseEntity<ApiResponse<String>> saveMemberBlock(
        @RequestBody MemberBlockRequestDto requestDto,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        memberBlockCommandService.saveMemberBlock(requestDto, memberDetails.getMember());
        return ResponseEntity.ok(ApiResponse.onSuccess("차단 완료"));
    }

    @GetMapping
    @Operation(summary = "[베로] 멤버 차단 목록 조회", description = "")
    public ResponseEntity<ApiResponse<List<MemberBlockResponseDto>>> getMemberBlockList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            memberBlockQueryService.getMemberBlockList(memberDetails.getMember())));
    }

    @DeleteMapping("/{memberId}")
    @Operation(summary = "[베로] 멤버 차단 해제", description = "path에 차단 해제할 멤버 id")
    @SwaggerApiError({
        ErrorStatus._CANNOT_BLOCK_REQUEST_SELF,
        ErrorStatus._ALREADY_NOT_BLOCKED_MEMBER
    })
    public ResponseEntity<ApiResponse<String>> deleteMemberBlock(@PathVariable Long memberId,
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        memberBlockCommandService.deleteMemberBlock(memberId, memberDetails.getMember());
        return ResponseEntity.ok(ApiResponse.onSuccess("차단 해제 완료"));
    }
}