package com.cozymate.cozymate_server.domain.memberstatpreference.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.memberstatpreference.dto.MemberStatPreferenceDto;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceCommandService;
import com.cozymate.cozymate_server.domain.memberstatpreference.service.MemberStatPreferenceQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/members/stat/preference")
@RequiredArgsConstructor
@RestController
public class MemberStatPreferenceController {

    private final MemberStatPreferenceCommandService memberStatPreferenceCommandService;
    private final MemberStatPreferenceQueryService memberStatPreferenceQueryService;

    @GetMapping("/")
    @Operation(summary = "[포비] 멤버 선호 항목 조회", description = "")
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_PREFERENCE_NOT_EXISTS
    })
    public ResponseEntity<ApiResponse<MemberStatPreferenceDto>> getMemberPreference(
        @AuthenticationPrincipal MemberDetails memberDetails) {

        return ResponseEntity.ok(ApiResponse.onSuccess(
            memberStatPreferenceQueryService.getPreferences(memberDetails.getMember().getId())));
    }

    @PostMapping("/")
    @Operation(summary = "[포비] 멤버 선호 항목 생성", description = ""
        + "선호 항목은 List<String>으로 주시면 됩니다")
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_PREFERENCE_PARAMETER_NOT_VALID
    })
    public ResponseEntity<ApiResponse<Long>> createMemberPreference(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody MemberStatPreferenceDto memberStatPreferenceDto
    ) {
        Long createdId = memberStatPreferenceCommandService.savePreferences(
            memberDetails.getMember().getId(),
            memberStatPreferenceDto.getPreferences());

        return ResponseEntity.ok(ApiResponse.onSuccess(createdId));
    }

    @PatchMapping("/")
    @Operation(summary = "[포비] 멤버 선호 항목 업데이트", description = ""
        + "선호 항목은 List<String>으로 주시면 됩니다")
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_PREFERENCE_PARAMETER_NOT_VALID
    })
    public ResponseEntity<ApiResponse<Long>> updateMemberPreference(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody MemberStatPreferenceDto memberStatPreferenceDto
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            memberStatPreferenceCommandService.updatePreferences(
                memberDetails.getMember().getId(),
                memberStatPreferenceDto.getPreferences()
            )
        ));
    }
}
