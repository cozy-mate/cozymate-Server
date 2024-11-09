package com.cozymate.cozymate_server.domain.favorite.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.favorite.dto.response.FavoriteMemberResponseDTO;
import com.cozymate.cozymate_server.domain.favorite.dto.response.FavoriteRoomResponseDTO;
import com.cozymate.cozymate_server.domain.favorite.service.FavoriteCommandService;
import com.cozymate.cozymate_server.domain.favorite.service.FavoriteQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteCommandService favoriteCommandService;
    private final FavoriteQueryService favoriteQueryService;

    @PostMapping("/members/{memberId}")
    @Operation(summary = "[베로] 사용자 찜하기", description = "memberId: 찜할 사용자 pk")
    @SwaggerApiError({
        ErrorStatus._FAVORITE_CANNOT_REQUEST_SELF,
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<String>> saveMemberFavorite(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long memberId) {
        favoriteCommandService.saveMemberFavorite(memberDetails.member(), memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess("사용자 찜 완료"));
    }

    @PostMapping("/rooms/{roomId}")
    @Operation(summary = "[베로] 방 찜하기", description = "roomId: 찜할 방 pk")
    @SwaggerApiError({
        ErrorStatus._FAVORITE_CANNOT_REQUEST_SELF,
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<String>> saveRoomFavorite(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long roomId) {
        favoriteCommandService.saveRoomFavorite(memberDetails.member(), roomId);
        return ResponseEntity.ok(ApiResponse.onSuccess("방 찜 완료"));
    }

    @DeleteMapping("/{favoriteId}")
    @Operation(summary = "[베로] 사용자/방 찜 삭제", description = "favoriteId: 찜 pk 값")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<String>> deleteFavorite(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long favoriteId) {
        favoriteCommandService.deleteFavorite(memberDetails.member(), favoriteId);
        return ResponseEntity.ok(ApiResponse.onSuccess("찜 삭제 완료"));
    }

    @GetMapping("/members")
    @Operation(summary = "[베로] 찜한 사용자 목록 조회", description = "")
    public ResponseEntity<ApiResponse<List<FavoriteMemberResponseDTO>>> getFavoriteMemberList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            favoriteQueryService.getFavoriteMemberList(memberDetails.member())));
    }

    @GetMapping("/rooms")
    @Operation(summary = "[베로] 찜한 방 목록 조회", description = "")
    public ResponseEntity<ApiResponse<List<FavoriteRoomResponseDTO>>> getFavoriteRoomList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            favoriteQueryService.getFavoriteRoomList(memberDetails.member())));
    }
}