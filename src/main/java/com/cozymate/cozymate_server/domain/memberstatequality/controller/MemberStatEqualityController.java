package com.cozymate.cozymate_server.domain.memberstatequality.controller;

import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityCommandService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/members/stat/equality")
@RequiredArgsConstructor
@RestController
public class MemberStatEqualityController {

    private final MemberStatEqualityCommandService memberStatEqualityCommandService;

    @Operation(
        summary = "[포비] 일치율 계산(관리자용)",
        description = "요청자의 토큰을 넣고, 일치율을 생성하고자 하는 멤버의 정보를 넣어 사용합니다.\n\n"
    )
    @PostMapping("/generate")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    public ResponseEntity<ApiResponse<Boolean>> generateMemberStatEquality(
    ) {
        memberStatEqualityCommandService.generateAllMemberStatEquality();
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                true
            ));
    }

    @Operation(
        summary = "[포비] 일치율 삭제(관리자용)",
        description = "요청자의 토큰을 넣고, 일치율을 삭제하고자 하는 멤버의 정보를 넣어 사용합니다.\n\n"
    )
    @DeleteMapping("/{memberId}")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    public ResponseEntity<ApiResponse<Boolean>> deleteMemberStatEqualityWithMemberId(
        @PathVariable Long memberId
    ) {
        memberStatEqualityCommandService.deleteMemberStatEqualitiesWithMemberId(memberId);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                true
            ));
    }

    @Operation(
        summary = "[포비] 일치율 재계산(관리자용)",
        description = "요청자의 토큰을 넣고, 일치율 정책이 바뀌어 재계산이 필요할 때 사용합니다.\n\n"
    )
    @PostMapping("/recalculate")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    public ResponseEntity<ApiResponse<Boolean>> deleteMemberStatEqualityWithMemberId(
    ) {
        memberStatEqualityCommandService.recalculateAllMemberStatEquality();
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                true
            ));
    }

    @Operation(
        summary = "[포비] 일치율 업데이트(관리자용)",
        description = "요청자의 토큰을 넣고, 일치율 정책이 바뀌어 재계산이 필요할 때 사용합니다.\n\n"
    )
    @PostMapping("/update")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    public ResponseEntity<ApiResponse<Boolean>> updateMemberStatEqualityWithMemberId(
    ) {
        memberStatEqualityCommandService.recalculateAllMemberStatEquality();
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                true
            ));
    }

}
