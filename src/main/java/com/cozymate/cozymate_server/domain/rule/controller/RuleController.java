package com.cozymate.cozymate_server.domain.rule.controller;

import com.cozymate.cozymate_server.domain.rule.dto.RuleRequestDto.CreateRuleRequestDto;
import com.cozymate.cozymate_server.domain.rule.dto.RuleResponseDto.RuleDetailResponseDto;
import com.cozymate.cozymate_server.domain.rule.service.RuleCommandService;
import com.cozymate.cozymate_server.domain.rule.service.RuleQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<String>> createRule(
        @PathVariable Long roomId,
        @RequestParam Long memberId,
        @Valid @RequestBody CreateRuleRequestDto createRuleRequestDto
    ) {
        ruleCommandService.createRule(roomId, memberId, createRuleRequestDto);
        return ResponseEntity.ok(ApiResponse.onSuccess("규칙 생성에 성공했습니다."));
    }

    @GetMapping("/{roomId}")
    @Operation(
        summary = "[무빗] 특정 방의 Rule 목록 조회",
        description = "memo는 null 반환이 가능합니다.")
    public ResponseEntity<ApiResponse<List<RuleDetailResponseDto>>> getRuleList(
        @PathVariable Long roomId,
        @RequestParam Long memberId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(ruleQueryService.getRule(roomId, memberId)));
    }

    @DeleteMapping("/{roomId}")
    @Operation(
        summary = "[무빗] 특정 방의 특정 Rule 삭제",
        description = "rule의 고유 번호로 삭제가 가능합니다.")
    public ResponseEntity<ApiResponse<String>> deleteRule(
        @PathVariable Long roomId,
        @RequestParam Long memberId,
        @RequestParam Long ruleId
    ) {
        ruleCommandService.deleteRule(roomId, memberId, ruleId);
        return ResponseEntity.ok(ApiResponse.onSuccess("삭제되었습니다."));
    }

}
