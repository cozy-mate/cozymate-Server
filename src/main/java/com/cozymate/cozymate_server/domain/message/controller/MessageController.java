package com.cozymate.cozymate_server.domain.message.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.message.dto.request.CreateMessageRequestDTO;
import com.cozymate.cozymate_server.domain.message.dto.response.MessageListResponseDTO;
import com.cozymate.cozymate_server.domain.message.service.MessageCommandService;
import com.cozymate.cozymate_server.domain.message.service.MessageQueryService;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomIdResponseDTO;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageCommandService messageCommandService;
    private final MessageQueryService messageQueryService;

    @PostMapping("/members/{recipientId}")
    @Operation(summary = "[베로] 쪽지 작성 기능", description = "recipientId: 쪽지를 받을 멤버의 pk값, RequestBody의 content: 쪽지 내용")
    @SwaggerApiError({
        ErrorStatus._MESSAGE_NOT_FOUND_RECIPIENT
    })
    public ResponseEntity<ApiResponse<MessageRoomIdResponseDTO>> createMessage(
        @Valid @RequestBody CreateMessageRequestDTO createMessageRequestDTO,
        @PathVariable Long recipientId, @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            messageCommandService.createMessage(createMessageRequestDTO, memberDetails.member(),
                recipientId)));
    }

    @GetMapping("/messagerooms/{messageRoomId}")
    @Operation(summary = "[베로] 쪽지방의 쪽지 상세 내역 조회 (수정 - 25.03.26)", description = "messageRoomId : 조회할 쪽지방 pk값")
    @SwaggerApiError({
        ErrorStatus._MESSAGEROOM_NOT_FOUND,
        ErrorStatus._MESSAGEROOM_MEMBERB_REQUIRED_WHEN_MEMBERA_NULL,
        ErrorStatus._MESSAGEROOM_MEMBERA_REQUIRED_WHEN_MEMBERB_NULL,
        ErrorStatus._MESSAGEROOM_INVALID_MEMBER
    })
    public ResponseEntity<ApiResponse<PageResponseDto<MessageListResponseDTO>>> getMessageList(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long messageRoomId,
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                messageQueryService.getMessageList(memberDetails.member(), messageRoomId, page, size)));
    }
}