package com.cozymate.cozymate_server.domain.memberblock.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.memberblock.dto.response.MemberBlockResponseDTO;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/block/members")
public class MemberBlockController {

    private final MemberBlockCommandService memberBlockCommandService;
    private final MemberBlockQueryService memberBlockQueryService;

    @PostMapping("/{memberId}")
    @Operation(summary = "[베로] 멤버 차단", description = "membreId: 차단할 사용자 pk")
    @SwaggerApiError({
        ErrorStatus._CANNOT_BLOCK_REQUEST_SELF,
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ALREADY_BLOCKED_MEMBER
    })
    public ResponseEntity<ApiResponse<String>> saveMemberBlock(@PathVariable Long memberId,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        memberBlockCommandService.saveMemberBlock(memberId, memberDetails.member());
        return ResponseEntity.ok(ApiResponse.onSuccess("차단 완료"));
    }

    @GetMapping
    @Operation(summary = "[베로] 멤버 차단 목록 조회", description = "")
    public ResponseEntity<ApiResponse<List<MemberBlockResponseDTO>>> getMemberBlockList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            memberBlockQueryService.getMemberBlockList(memberDetails.member())));
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
        memberBlockCommandService.deleteMemberBlock(memberId, memberDetails.member());
        return ResponseEntity.ok(ApiResponse.onSuccess("차단 해제 완료"));
    }
}