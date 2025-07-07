package com.cozymate.cozymate_server.domain.feed.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.feed.dto.FeedRequestDTO;
import com.cozymate.cozymate_server.domain.feed.dto.FeedResponseDTO;
import com.cozymate.cozymate_server.domain.feed.service.FeedCommandService;
import com.cozymate.cozymate_server.domain.feed.service.FeedQueryService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/feed")
@RequiredArgsConstructor
@RestController
public class FeedController {

    private final FeedCommandService feedCommandService;
    private final FeedQueryService feedQueryService;

    @Deprecated
    @Operation(
        summary = "[포비] 피드 정보 등록하기",
        description = "해당 API는 더 이상 사용하지 않습니다"
    )
    @SwaggerApiError({
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND,
        ErrorStatus._FEED_EXISTS
    })
    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> createFeedInfo(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody FeedRequestDTO feedRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                feedCommandService.createFeedInfo(memberDetails.member(), feedRequestDTO)));
    }

    @Operation(
        summary = "[포비] 피드 정보 수정하기",
        description = "사용자의 토큰을 넣어 사용하고, body로 룸 ID와 피드 상세정보를 넣어 사용합니다.\n\n"
    )
    @SwaggerApiError({
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND,
        ErrorStatus._FEED_NOT_EXISTS
    })
    @PutMapping("")
    public ResponseEntity<ApiResponse<Long>> updateFeedInfo(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody FeedRequestDTO feedRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                feedCommandService.updateFeedInfo(memberDetails.member(), feedRequestDTO)));
    }

    @Operation(
        summary = "[포비] 피드 정보 조회하기",
        description = "사용자의 토큰을 넣어 사용하고, Path Variable로 룸 ID를 넣어 사용합니다.\n\n"
    )
    @SwaggerApiError({
        ErrorStatus._MATE_OR_ROOM_NOT_FOUND,
        ErrorStatus._FEED_NOT_EXISTS,
    })
    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<FeedResponseDTO>> getFeedInfo(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                feedQueryService.getFeedInfo(memberDetails.member(),roomId)));
    }
}
