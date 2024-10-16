package com.cozymate.cozymate_server.domain.memberstatequality.controller;

import com.cozymate.cozymate_server.domain.memberstatequality.service.MemberStatEqualityCommandService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("")
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

}
