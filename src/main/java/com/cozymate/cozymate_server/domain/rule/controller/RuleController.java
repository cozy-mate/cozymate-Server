package com.cozymate.cozymate_server.domain.rule.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.rule.dto.RuleRequestDto.CreateRuleRequestDto;
import com.cozymate.cozymate_server.domain.rule.dto.RuleResponseDto.CreateRuleResponseDto;
import com.cozymate.cozymate_server.domain.rule.dto.RuleResponseDto.RuleDetailResponseDto;
import com.cozymate.cozymate_server.domain.rule.service.RuleCommandService;
import com.cozymate.cozymate_server.domain.rule.service.RuleQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RuleController {

    private final RuleCommandService ruleCommandService;
    private final RuleQueryService ruleQueryService;

    /**
     * 특정 방의 Rule 생성
     *
     * @param memberDetails 사용자
     * @param roomId        Rule을 생성하려는 방 Id
     * @param requestDto    생성할 Rule 데이터
     * @return String 성공 메시지 반환
     */
    @PostMapping("/{roomId}/rules")
    @Operation(
        summary = "[무빗] 특정 방의 Rule 생성",
        description = "rule 내용은 필수, memo는 선택입니다.")
    @SwaggerApiError({ErrorStatus._MATE_OR_ROOM_NOT_FOUND, ErrorStatus._RULE_OVER_MAX})
    public ResponseEntity<ApiResponse<CreateRuleResponseDto>> createRule(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @RequestBody @Valid CreateRuleRequestDto requestDto
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            ruleCommandService.createRule(
                memberDetails.getMember(), roomId, requestDto
            )
        ));
    }

    /**
     * 특정 방의 Rule 목록 조회
     *
     * @param memberDetails 사용자
     * @param roomId        Rule 목록을 조회하려는 방
     * @return List<RuleDetailResponseDto> Rule 목록 반환
     */
    @GetMapping("/{roomId}/rules")
    @Operation(
        summary = "[무빗] 특정 방의 Rule 목록 조회",
        description = "Rule에서 memo는 null 반환이 가능합니다.")
    @SwaggerApiError({ErrorStatus._MATE_OR_ROOM_NOT_FOUND})
    public ResponseEntity<ApiResponse<List<RuleDetailResponseDto>>> getRuleList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            ruleQueryService.getRule(memberDetails.getMember(), roomId)
        ));
    }

    /**
     * 특정 방의 특정 Rule 삭제
     *
     * @param memberDetails 사용자
     * @param roomId        삭제하려는 Rule이 속한 방 Id
     * @param ruleId        삭제하려는 Rule Id
     * @return String 성공 메시지 반환
     */
    @DeleteMapping("/{roomId}/rules/{ruleId}")
    @Operation(
        summary = "[무빗] 특정 Rule 삭제",
        description = "rule의 고유 번호로 삭제가 가능합니다.")
    @SwaggerApiError({ErrorStatus._MATE_OR_ROOM_NOT_FOUND, ErrorStatus._RULE_NOT_FOUND,
        ErrorStatus._RULE_MATE_MISMATCH})
    public ResponseEntity<ApiResponse<String>> deleteRule(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @PathVariable @Positive Long ruleId
    ) {
        ruleCommandService.deleteRule(memberDetails.getMember(), roomId, ruleId);
        return ResponseEntity.ok(ApiResponse.onSuccess("삭제되었습니다."));
    }

    // TODO: Rule 수정 API 추가

    /**
     * 특정 Rule 수정
     *
     * @param memberDetails 사용자
     * @param roomId        수정하고자 하는 rule이 속한 방의 고유 번호
     * @param ruleId        수정하고자 하는 rule의 고유 번호
     * @return String 성공 메시지 반환
     */
    @PutMapping("/{roomId}/rules/{ruleId}")
    @Operation(
        summary = "[무빗] 특정 Rule 수정",
        description = "rule의 고유 번호로 수정이 가능합니다.")
    @SwaggerApiError({})
    public ResponseEntity<ApiResponse<String>> updateRule(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @PathVariable @Positive Long ruleId,
        @RequestBody @Valid CreateRuleRequestDto requestDto
    ) {
        ruleCommandService.updateRule(memberDetails.getMember(), roomId, ruleId, requestDto);
        return ResponseEntity.ok(ApiResponse.onSuccess("수정되었습니다."));
    }
}
