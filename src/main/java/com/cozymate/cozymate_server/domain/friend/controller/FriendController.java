package com.cozymate.cozymate_server.domain.friend.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.friend.dto.FriendRequestDTO;
import com.cozymate.cozymate_server.domain.friend.dto.FriendResponseDTO.FriendLikeResponseDTO;
import com.cozymate.cozymate_server.domain.friend.dto.FriendResponseDTO.FriendSummaryResponseDTO;
import com.cozymate.cozymate_server.domain.friend.enums.FriendStatus;
import com.cozymate.cozymate_server.domain.friend.service.FriendCommandService;
import com.cozymate.cozymate_server.domain.friend.service.FriendQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/friend")
@RequiredArgsConstructor
@RestController
public class FriendController {

    private final FriendCommandService friendCommandService;
    private final FriendQueryService friendQueryService;

    @Deprecated
    @Operation(
        summary = "[포비] 친구 신청 요청",
        description = "보내는 사용자의 토큰을 넣어 사용하고, Body로 친구 요청을 받는 멤버의 ID를 보내주세요."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._FRIEND_REQUEST_SENT,
        ErrorStatus._FRIEND_REQUEST_RECEIVED
    })
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Long>> createFriendRequest(
        @AuthenticationPrincipal MemberDetails senderDetails,
        @RequestBody @Valid FriendRequestDTO sendFriendRequestDTO) {

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                friendCommandService.requestFriend(senderDetails.getMember(), sendFriendRequestDTO)));
    }

    @Deprecated
    @Operation(
        summary = "[포비] 친구 신청 수락",
        description = "수락하는 사용자의 토큰을 넣어 사용하고, Body로 친구 신청을 보냈던 멤버의 ID를 보내주세요."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._FRIEND_REQUEST_NOT_FOUND,
        ErrorStatus._FRIEND_REQUEST_RECEIVED
    })
    @PutMapping("/accept")
    public ResponseEntity<ApiResponse<Long>> acceptFriendRequest(
        @AuthenticationPrincipal MemberDetails receiverDetails,
        @RequestBody @Valid FriendRequestDTO sendFriendRequestDTO) {

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                friendCommandService.acceptFriendRequest(receiverDetails.getMember(), sendFriendRequestDTO)));
    }

    @Deprecated
    @Operation(
        summary = "[포비] 친구 신청 거절",
        description = "수락하는 사용자의 토큰을 넣어 사용하고, Body로 친구 신청을 보냈던 멤버의 ID를 보내주세요."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._FRIEND_REQUEST_NOT_FOUND,
        ErrorStatus._FRIEND_REQUEST_ACCEPTED
    })
    @DeleteMapping("/deny")
    public ResponseEntity<ApiResponse<Long>> denyFriendRequest(
        @AuthenticationPrincipal MemberDetails receiverDetails,
        @RequestBody @Valid FriendRequestDTO sendFriendRequestDTO) {

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                friendCommandService.denyFriendRequest(receiverDetails.getMember(), sendFriendRequestDTO)));
    }

    @Deprecated
    @Operation(
        summary = "[포비] 친구 좋아요 토글",
        description = "사용자의 토큰을 넣어 사용하고, Body로 좋아하고자 하는 멤버의 ID를 보내주세요."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._FRIEND_REQUEST_NOT_FOUND,
        ErrorStatus._FRIEND_REQUEST_RECEIVED,
        ErrorStatus._FRIEND_REQUEST_WAITING
    })
    @PutMapping("/toggle-like")
    public ResponseEntity<ApiResponse<FriendLikeResponseDTO>> likeFriendRequest(
        @AuthenticationPrincipal MemberDetails likerDetails,
        @RequestBody @Valid FriendRequestDTO sendFriendRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                friendCommandService.toggleLikeFriend(likerDetails.getMember(), sendFriendRequestDTO)));
    }

    @Deprecated
    @Operation(
        summary = "[포비] 친구 목록 가져오기",
        description = "사용자의 토큰을 넣어 사용하고,"
            + "친구가 없을 경우 빈 배열을 리턴합니다."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND
    })
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<FriendSummaryResponseDTO>>> getFriendList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                friendQueryService.getFriendList(memberDetails.getMember())
            ));
    }

    @Deprecated
    @Operation(
        summary = "[포비] 친구 여부 가져오기",
        description = "사용자의 토큰을 넣어 사용하고,\n"
            + "친구 상태를 아래와 같은 문자열로 리턴합니다.\n"
            + "Android는 ENUM, iOS는 타입이나 인터페이스로 정의해 처리하시면 되겠습니다.\n"
            + "- STRANGER -> 친구 아님\n"
            + "- WAITING -> 친구 요청 대기중\n"
            + "- ACCEPT -> 친구 요청 대기중\n"
    )
    @SwaggerApiError({
        ErrorStatus._FRIEND_REQUEST_EQUAL
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<FriendStatus>> getFriendStatus(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long memberId) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                friendQueryService.getFriendStatus(memberDetails.getMember(), memberId)
            ));
    }
}
