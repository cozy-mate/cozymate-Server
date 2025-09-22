package com.cozymate.cozymate_server.domain.messageroom.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.messageroom.MessageRoom;
import com.cozymate.cozymate_server.domain.messageroom.converter.MessageRoomConverter;
import com.cozymate.cozymate_server.domain.messageroom.dto.MessageRoomSimpleDTO;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.CountMessageRoomsWithNewMessageDTO;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.messageroom.dto.response.MessageRoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.messageroom.service.MessageRoomCommandService;
import com.cozymate.cozymate_server.domain.messageroom.service.MessageRoomQueryService;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/messagerooms")
public class MessageRoomController {

    private final MessageRoomCommandService messageRoomCommandService;
    private final MessageRoomQueryService messageRoomQueryService;

    @DeleteMapping("/{messageRoomId}")
    @Operation(summary = "[베로] 쪽지방 삭제 기능", description = "messageRoomId : 나갈 쪽지방 pk")
    @SwaggerApiError({
        ErrorStatus._MESSAGEROOM_NOT_FOUND,
        ErrorStatus._MESSAGEROOM_FORBIDDEN
    })
    public ResponseEntity<ApiResponse<String>> deleteMessageRoom(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long messageRoomId) {
        messageRoomCommandService.deleteMessageRoom(memberDetails.member(), messageRoomId);
        return ResponseEntity.ok(ApiResponse.onSuccess("쪽지방 삭제 완료"));
    }

    @GetMapping
    @Operation(summary = "[베로] 쪽지방 목록 조회 (수정 - 25.03.26)", description = "")
    public ResponseEntity<ApiResponse<PageResponseDto<List<MessageRoomDetailResponseDTO>>>> getMessageRoomList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                messageRoomQueryService.getMessageRoomList(memberDetails.member(), page, size)));
    }

    @GetMapping("/members/{recipientId}")
    @Operation(summary = "[베로] 쪽지방 반환", description = "")
    @SwaggerApiError(
        ErrorStatus._MEMBER_NOT_FOUND
    )
    public ResponseEntity<ApiResponse<MessageRoomIdResponseDTO>> getMessageRoom(
        @AuthenticationPrincipal MemberDetails memberDetails, @PathVariable Long recipientId) {
        Member member = memberDetails.member();

        MessageRoomSimpleDTO simpleDTO = messageRoomQueryService.getMessageRoom(member, recipientId);
        Optional<MessageRoom> messageRoom = simpleDTO.messageRoom();
        if (messageRoom.isPresent()) {
            return ResponseEntity.ok(ApiResponse.onSuccess(
                MessageRoomConverter.toMessageRoomIdResponseDTO(messageRoom.get().getId())));
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(
            messageRoomCommandService.saveMessageRoom(member, simpleDTO.recipient())));
    }

    @GetMapping("/count/new-message")
    @Operation(summary = "[베로] 새로운 쪽지가 온 쪽지방의 갯수 반환", description = "")
    @SwaggerApiError(
        ErrorStatus._MEMBER_NOT_FOUND
    )
    public ResponseEntity<ApiResponse<CountMessageRoomsWithNewMessageDTO>> getCountMessageRoomsWithNewMessage(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(
            messageRoomQueryService.countMessageRoomsWithNewMessage(memberDetails.member())));
    }
}