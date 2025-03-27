package com.cozymate.cozymate_server.domain.roomfavorite.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.roomfavorite.dto.response.RoomFavoriteResponseDTO;
import com.cozymate.cozymate_server.domain.roomfavorite.service.RoomFavoriteCommandService;
import com.cozymate.cozymate_server.domain.roomfavorite.service.RoomFavoriteQueryService;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites/rooms")
public class RoomFavoriteController {

    private final RoomFavoriteCommandService roomFavoriteCommandService;
    private final RoomFavoriteQueryService roomFavoriteQueryService;

    @PostMapping("/{roomId}")
    @Operation(summary = "[베로] 방 찜하기", description = "roomId: 찜할 방 pk")
    @SwaggerApiError({
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOMFAVORITE_CANNOT_PRIVATE_ROOM,
        ErrorStatus._ROOMFAVORITE_CANNOT_FULL_ROOM,
        ErrorStatus._ROOMFAVORITE_CANNOT_DISABLE_ROOM,
        ErrorStatus._ROOMFAVORITE_ALREADY_EXISTS,
    })
    public ResponseEntity<ApiResponse<String>> saveRoomFavorite(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long roomId) {
        roomFavoriteCommandService.saveRoomFavorite(memberDetails.member(), roomId);
        return ResponseEntity.ok(ApiResponse.onSuccess("방 찜 완료"));
    }

    @GetMapping
    @Operation(summary = "[베로] 찜한 방 목록 조회 (수정 - 25.03.27)", description = "")
    public ResponseEntity<ApiResponse<PageResponseDto<List<RoomFavoriteResponseDTO>>>> getFavoriteRoomList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            roomFavoriteQueryService.getFavoriteRoomList(memberDetails.member(), page, size)));
    }

    @DeleteMapping("/{roomFavoriteId}")
    @Operation(summary = "[베로] 방 찜 삭제", description = "roomFavoriteId: 방 찜 pk 값")
    @SwaggerApiError({
        ErrorStatus._ROOMFAVORITE_NOT_FOUND,
        ErrorStatus._ROOMFAVORITE_MEMBER_MISMATCH
    })
    public ResponseEntity<ApiResponse<String>> deleteRoomFavorite(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long roomFavoriteId) {
        roomFavoriteCommandService.deleteRoomFavorite(memberDetails.member(), roomFavoriteId);
        return ResponseEntity.ok(ApiResponse.onSuccess("찜 삭제 완료"));
    }
}
