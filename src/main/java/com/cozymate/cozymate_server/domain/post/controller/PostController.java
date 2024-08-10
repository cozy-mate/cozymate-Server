package com.cozymate.cozymate_server.domain.post.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.feed.dto.FeedRequestDTO;
import com.cozymate.cozymate_server.domain.post.dto.PostRequestDTO;
import com.cozymate.cozymate_server.domain.post.service.PostCommandService;
import com.cozymate.cozymate_server.domain.post.service.PostQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/post")
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    @Operation(
        summary = "[포비] 피드 정보 등록하기",
        description = "사용자의 토큰을 넣어 사용하고, body로 룸 ID와 피드 상세정보를 넣어 사용합니다.\n\n"
    )
    @SwaggerApiError({})
    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> createMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody PostRequestDTO postRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                null));
    }

    @Operation(
        summary = "[포비] 피드 정보 등록하기",
        description = "사용자의 토큰을 넣어 사용하고, body로 룸 ID와 피드 상세정보를 넣어 사용합니다.\n\n"
    )
    @SwaggerApiError({})
    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> createMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody PostRequestDTO postRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                null));
    }
}
