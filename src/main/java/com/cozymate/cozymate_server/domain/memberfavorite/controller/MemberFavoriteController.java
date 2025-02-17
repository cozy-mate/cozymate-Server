package com.cozymate.cozymate_server.domain.memberfavorite.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.memberfavorite.dto.response.MemberFavoriteResponseDTO;
import com.cozymate.cozymate_server.domain.memberfavorite.service.MemberFavoriteCommandService;
import com.cozymate.cozymate_server.domain.memberfavorite.service.MemberFavoriteQueryService;
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
@RequestMapping("/favorites/members")
public class MemberFavoriteController {

    private final MemberFavoriteCommandService memberFavoriteCommandService;
    private final MemberFavoriteQueryService memberFavoriteQueryService;

    @PostMapping("/{memberId}")
    @Operation(summary = "[베로] 사용자 찜하기", description = "memberId: 찜할 사용자 pk")
    @SwaggerApiError({
        ErrorStatus._MEMBERFAVORITE_CANNOT_REQUEST_SELF,
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._MEMBERFAVORITE_ALREADY_EXISTS,
        ErrorStatus._MEMBERFAVORITE_CANNOT_FAVORITE_MEMBER_WITHOUT_MEMBERSTAT
    })
    public ResponseEntity<ApiResponse<String>> saveMemberFavorite(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long memberId) {
        memberFavoriteCommandService.saveMemberFavorite(memberDetails.member(), memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess("사용자 찜 완료"));
    }

    @GetMapping
    @Operation(summary = "[베로] 찜한 사용자 목록 조회", description = "")
    public ResponseEntity<ApiResponse<List<MemberFavoriteResponseDTO>>> getMemberFavoriteList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            memberFavoriteQueryService.getMemberFavoriteList(memberDetails.member())));
    }

    @DeleteMapping("/{memberFavoriteId}")
    @Operation(summary = "[베로] 사용자 찜 삭제", description = "memberFavoriteId: 사용자 찜 pk")
    @SwaggerApiError({
        ErrorStatus._MEMBERFAVORITE_NOT_FOUND,
        ErrorStatus._MEMBERFAVORITE_MEMBER_MISMATCH
    })
    public ResponseEntity<ApiResponse<String>> deleteMemberFavorite(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long memberFavoriteId) {
        memberFavoriteCommandService.deleteMemberFavorite(memberDetails.member(), memberFavoriteId);
        return ResponseEntity.ok(ApiResponse.onSuccess("찜 삭제 완료"));
    }
}
