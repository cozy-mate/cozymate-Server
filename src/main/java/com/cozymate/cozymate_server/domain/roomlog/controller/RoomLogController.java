package com.cozymate.cozymate_server.domain.roomlog.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.roomlog.dto.response.RoomLogDetailResponseDTO;
import com.cozymate.cozymate_server.domain.roomlog.service.RoomLogQueryService;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roomlog")
public class RoomLogController {

    private final RoomLogQueryService roomLogQueryService;

    @GetMapping("/{roomId}")
    @Operation(summary = "[무빗] 특정 방에 roomlog 목록 조회", description = "해당 방의 로그가 출력됩니다.")
    @SwaggerApiError({ErrorStatus._MATE_OR_ROOM_NOT_FOUND})
    public ResponseEntity<ApiResponse<PageResponseDto<List<RoomLogDetailResponseDTO>>>> getRoomLogList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(ApiResponse.onSuccess(
            roomLogQueryService.getRoomLogList(roomId, memberDetails.member(), page, size)
        ));
    }

}
