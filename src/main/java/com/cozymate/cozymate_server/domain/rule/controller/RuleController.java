package com.cozymate.cozymate_server.domain.rule.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.rule.dto.RuleRequestDto.CreateRuleRequestDto;
import com.cozymate.cozymate_server.domain.rule.dto.RuleResponseDto.RuleDetailResponseDto;
import com.cozymate.cozymate_server.domain.rule.service.RuleCommandService;
import com.cozymate.cozymate_server.domain.rule.service.RuleQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rule")
public class RuleController {

    private final RuleCommandService ruleCommandService;
    private final RuleQueryService ruleQueryService;

    @PostMapping("/{roomId}")
    @Operation(
        summary = "[무빗] 특정 방의 Rule 생성",
        description = "rule 내용은 필수, memo는 선택입니다.")
    @SwaggerApiError({ErrorStatus._MATE_OR_ROOM_NOT_FOUND, ErrorStatus._RULE_OVER_MAX})
    public ResponseEntity<ApiResponse<String>> createRule(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId,
        @Valid @RequestBody CreateRuleRequestDto createRuleRequestDto
    ) {
        ruleCommandService.createRule(memberDetails.getMember(), roomId, createRuleRequestDto);
        return ResponseEntity.ok(ApiResponse.onSuccess("규칙 생성에 성공했습니다."));
    }

    @GetMapping("/{roomId}")
    @Operation(
        summary = "[무빗] 특정 방의 Rule 목록 조회",
        description = "Rule에서 memo는 null 반환이 가능합니다.")
    @SwaggerApiError({ErrorStatus._MATE_OR_ROOM_NOT_FOUND, ErrorStatus._RULE_NOT_FOUND,
        ErrorStatus._RULE_MATE_MISMATCH})
    public ResponseEntity<ApiResponse<List<RuleDetailResponseDto>>> getRuleList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            ruleQueryService.getRule(roomId, memberDetails.getMember())
        ));
    }

    @DeleteMapping("/{roomId}")
    @Operation(
        summary = "[무빗] 특정 방의 특정 Rule 삭제",
        description = "rule의 고유 번호로 삭제가 가능합니다.")
    @SwaggerApiError({ErrorStatus._MATE_OR_ROOM_NOT_FOUND, ErrorStatus._RULE_NOT_FOUND,
        ErrorStatus._RULE_MATE_MISMATCH})
    public ResponseEntity<ApiResponse<String>> deleteRule(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId,
        @RequestParam Long ruleId
    ) {
        ruleCommandService.deleteRule(memberDetails.getMember(), roomId, ruleId);
        return ResponseEntity.ok(ApiResponse.onSuccess("삭제되었습니다."));
    }

}
