package com.cozymate.cozymate_server.domain.friend.controller;

import com.cozymate.cozymate_server.domain.friend.dto.FriendRequestDTO;
import com.cozymate.cozymate_server.domain.friend.service.FriendCommandService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;
import com.sun.net.httpserver.Authenticator.Success;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/friend")
@RequiredArgsConstructor
@RestController
public class FriendController {

    private final FriendCommandService friendCommandService;

    /**
     * TODO: member는 추후 시큐리티 인증 객체에서 받아오는 것으로 변경 예정, path 변경 예정 사항("/memberId" -> "/")
     */
    @Operation(
        summary = "[포비] 친구 신청 요청",
        description = "Path Variable로 보내는 멤버의 ID, Body로 받는 멤버의 ID를 보내주세요."
    )
    @PostMapping("/request/{senderId}")
    public ResponseEntity<ApiResponse<Long>> createFriendRequest(
        @PathVariable Long senderId,
        @RequestBody @Valid FriendRequestDTO sendFriendRequestDTO) {

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                friendCommandService.requestFriend(senderId, sendFriendRequestDTO)));
    }

    /**
     * TODO: member는 추후 시큐리티 인증 객체에서 받아오는 것으로 변경 예정, path 변경 예정 사항("/memberId" -> "/")
     */
    @Operation(
        summary = "[포비] 친구 신청 수락",
        description = "Path Variable로 보내는 멤버의 ID, Body로 받는 멤버의 ID를 보내주세요."
    )
    @PutMapping("/accept/{accepterId}")
    public ResponseEntity<ApiResponse<Long>> acceptFriendRequest(
        @PathVariable Long accepterId,
        @RequestBody @Valid FriendRequestDTO sendFriendRequestDTO) {

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                friendCommandService.acceptFriendRequest(accepterId, sendFriendRequestDTO)));
    }

    /**
     * TODO: member는 추후 시큐리티 인증 객체에서 받아오는 것으로 변경 예정, path 변경 예정 사항("/memberId" -> "/")
     */
    @Operation(
        summary = "[포비] 친구 신청 삭제",
        description = "Path Variable로 보내는 멤버의 ID, Body로 받는 멤버의 ID를 보내주세요."
    )
    @DeleteMapping("/deny/{accepterId}")
    public ResponseEntity<ApiResponse<Long>> denyFriendRequest(
        @PathVariable Long accepterId,
        @RequestBody @Valid FriendRequestDTO sendFriendRequestDTO) {

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                friendCommandService.denyFriendRequest(accepterId, sendFriendRequestDTO)));
    }
}
