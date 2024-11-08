package com.cozymate.cozymate_server.domain.room.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.room.dto.RoomRecommendResponseDto.RoomRecommendationResponseList;
import com.cozymate.cozymate_server.domain.room.service.RoomRecommendService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomRecommendController {

    private final RoomRecommendService roomRecommendService;

    @GetMapping("/list")
    @Operation(summary = "[무빗] 방 추천 리스트 조회", description = "방 추천 리스트를 조건별로 조회가 가능합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_PREFERENCE_NOT_EXISTS
    })
    public ResponseEntity<ApiResponse<RoomRecommendationResponseList>> getRoomList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam @Positive @Max(10) int size
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            roomRecommendService.getRecommendationList(memberDetails.member(), size)));
    }
}
