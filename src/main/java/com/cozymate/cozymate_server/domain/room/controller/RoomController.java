package com.cozymate.cozymate_server.domain.room.controller;


import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.room.dto.CozymateResponse;
import com.cozymate.cozymate_server.domain.room.dto.InviteRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomRequestDto;
import com.cozymate.cozymate_server.domain.room.dto.RoomRequestDto.RoomUpdateRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomResponseDto.RoomCreateResponse;
import com.cozymate.cozymate_server.domain.room.dto.RoomResponseDto.RoomExistResponse;
import com.cozymate.cozymate_server.domain.room.dto.RoomResponseDto.RoomJoinResponse;
import com.cozymate.cozymate_server.domain.room.service.RoomCommandService;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomCommandService roomCommandService;
    private final RoomQueryService roomQueryService;

    @PostMapping("/create-private")
    @Operation(summary = "[바니] 초대코드로 방생성 기능", description = "방이름, 프로필이미지, 인원수를 입력합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_ALREADY_EXISTS
    })
    public ResponseEntity<ApiResponse<RoomCreateResponse>> createRoom(@Valid @RequestBody RoomRequestDto.PrivateRoomCreateRequest request,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        RoomCreateResponse response = roomCommandService.createPrivateRoom(request, memberDetails.getMember());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PostMapping("/create-public")
    @Operation(summary = "[바니]공개 방 생성 기능", description = "방이름, 프로필이미지, 인원수, 해시태그(1-3개)를 입력합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_ALREADY_EXISTS,
        ErrorStatus._DUPLICATE_HASHTAGS
    })
    public ResponseEntity<ApiResponse<RoomCreateResponse>> createPublicRoom(
        @Valid @RequestBody RoomRequestDto.PublicRoomCreateRequest request,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        RoomCreateResponse response = roomCommandService.createPublicRoom(request, memberDetails.getMember());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "[바니] 방 정보 조회 기능", description = "방 아이디를 입력합니다. 공개방만 모두 조회 가능하고, 비공개 방은 사용자가 속한 방만 조회 가능합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE
    })
    public ResponseEntity<ApiResponse<RoomCreateResponse>> getRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        RoomCreateResponse response = roomQueryService.getRoomById(roomId, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "[바니] 방 삭제 기능 (방장 권한)", description = "해당 roomId의 방을 삭제합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER
    })
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        roomCommandService.deleteRoom(roomId, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 삭제 완료"));
    }

    @PatchMapping("/{roomId}/quit")
    @Operation(summary = "[바니] 방 나가기 기능", description = "해당 roomId의 방을 나갑니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE
    })
    public ResponseEntity<ApiResponse<String>> quitRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        roomCommandService.quitRoom(roomId, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 나가기 완료"));
    }

    @GetMapping("/join")
    @Operation(summary = "[바니] 초대코드로 방 정보 조회 기능", description = "초대코드를 입력하면 방 정보를 조회합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER
    })
    public ResponseEntity<ApiResponse<RoomJoinResponse>> getRoomInfo(@RequestParam String inviteCode,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        RoomJoinResponse roomJoinResponse = roomQueryService.getRoomByInviteCode(inviteCode, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(roomJoinResponse));
    }

    @PostMapping("/{roomId}/join")
    @Operation(summary = "[바니] 방 입장 기능", description = "해당 roomId에 참여합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOM_ALREADY_JOINED,
        ErrorStatus._ROOM_ALREADY_EXISTS,
        ErrorStatus._ROOM_FULL
    })
    public ResponseEntity<ApiResponse<String>> joinRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        roomCommandService.joinRoom(roomId, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 참여 완료"));
    }

    @GetMapping("/check-roomname")
    @Operation(summary = "[바니] 방 이름 중복 검증", description = "가능하면 true가, 중복시 false가 리턴됩니다.")
    ResponseEntity<ApiResponse<Boolean>> checkRoomName(@RequestParam String roomName) {
        Boolean isValid = roomCommandService.checkRoomName(roomName);
        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus()).body(ApiResponse.onSuccess(isValid));
    }

    @GetMapping("/{roomId}/available-friends")
    @Deprecated
    @Operation(summary = "[바니] 방에 초대할 코지메이트 목록 조회", description = "로그인한 멤버의 코지메이트 목록을 불러옵니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<List<CozymateResponse>>> getCozymateList(
        @PathVariable Long roomId, @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(roomQueryService.getCozymateList(roomId, memberDetails.getMember().getId())));
    }

    @PostMapping("/invite/{inviteeId}")
    @Operation(summary = "[바니] 방장 -> 내방으로 초대하기", description = "방장이 속해있는 roomId에 선택한 코지메이트를 초대합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER,
        ErrorStatus._ROOM_FULL,
        ErrorStatus._INVITATION_ALREADY_SENT,
        ErrorStatus._ROOM_ALREADY_JOINED,
        ErrorStatus._ROOM_ALREADY_EXISTS
    })
    public ResponseEntity<ApiResponse<String>> inviteCozymate(
        @PathVariable Long inviteeId, @AuthenticationPrincipal MemberDetails inviterDetails) {
        roomCommandService.sendInvitation(inviteeId, inviterDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 초대 요청 완료"));
    }

    @GetMapping ("/request-invites")
    @Deprecated
    @Operation(summary = "[바니] 방 초대 요청 조회", description = "해당 사용자가 수신한 초대 요청을 조회합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._INVITATION_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<InviteRequest>> getRequestInvite(@AuthenticationPrincipal MemberDetails inviteeDetails) {
        InviteRequest inviteRequest = roomQueryService.getInvitation(inviteeDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(inviteRequest));
    }

    @PostMapping("/{roomId}/invite-request")
    @Operation(summary = "[바니] 사용자 -> 방 초대 요청/수락", description = "해당 roomId에서 온 초대요청을 수락 또는 거절합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOM_FULL,
        ErrorStatus._INVITATION_NOT_FOUND,
        ErrorStatus._ROOM_ALREADY_EXISTS
    })
    public ResponseEntity<ApiResponse<String>> respondToInvitation(
        @PathVariable Long roomId, @AuthenticationPrincipal MemberDetails inviteeDetails, @RequestParam boolean accept) {
        roomCommandService.respondToInvitation(roomId, inviteeDetails.getMember().getId(), accept);
        return ResponseEntity.ok(ApiResponse.onSuccess(accept ? "초대 요청 수락 완료" : "초대 요청 거절 완료"));
    }

    @GetMapping("/exist")
    @Operation(summary = "[바니] 사용자가 참여한 방이 있는지 여부 조회", description = "현재 참여중인 방이 있다면 해당 roomId가, 없다면 roomId값이 0으로 리턴됩니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<RoomExistResponse>> getExistRoom(@AuthenticationPrincipal MemberDetails memberDetails) {
        RoomExistResponse response = roomQueryService.getExistRoom(memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PatchMapping("/{roomId}")
    @Operation(summary = "[바니] 방 정보 수정", description = "해당 roomId의 정보를 수정합니다. (초대방은 방 이름만, 공개방은 방이름/해시태그 수정)")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE,
        ErrorStatus._NOT_ROOM_MANAGER
    }
    )
    public ResponseEntity<ApiResponse<RoomCreateResponse>> updateRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody RoomUpdateRequest request) {
        RoomCreateResponse response = roomCommandService.updateRoom(roomId, memberDetails.getMember().getId(), request);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));

    }

    @PatchMapping("{roomId}/force-quit/{memberId}")
    @Operation(summary = "[바니] 방에서 강제 퇴장 시키기", description = "방장이 해당 roomId의 특정 룸메이트를 강제퇴장 시킵니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE,
        ErrorStatus._NOT_ROOM_MANAGER,
        ErrorStatus._CANNOT_SELF_FORCED_QUIT
    })
    public ResponseEntity<ApiResponse<String>> forceQuitRoom(
        @PathVariable Long roomId, @PathVariable Long memberId,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        roomCommandService.forceQuitRoom(roomId, memberId, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("강제 퇴장 완료"));
    }

    @DeleteMapping("/invitee/{inviteeId}")
    @Operation(summary = "[바니] 방장 -> 내방으로 초대 취소 기능", description = "내방으로 초대했던 inviteeId를 이용해 초대를 취소합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER,
        ErrorStatus._INVITATION_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<String>> cancelInvitation(
        @PathVariable Long inviteeId,
        @AuthenticationPrincipal MemberDetails inviterDetails) {
        roomCommandService.cancelInvitation(inviteeId, inviterDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("초대 취소 완료"));
    }

    @PostMapping("/{roomId}/request-join")
    @Operation(summary = "[바니] 사용자 -> 방 참여 요청", description = "해당 roomId에 참여 요청을 보냅니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOM_ALREADY_JOINED,
        ErrorStatus._ROOM_ALREADY_EXISTS,
        ErrorStatus._REQUEST_ALREADY_SENT,
        ErrorStatus._INVITATION_ALREADY_SENT,
        ErrorStatus._ROOM_FULL
    })
    public ResponseEntity<ApiResponse<String>> requestToJoin(
        @PathVariable Long roomId, @AuthenticationPrincipal MemberDetails memberDetails) {
        roomCommandService.requestToJoin(roomId, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 참여 요청 완료"));
    }

    @DeleteMapping("/{roomId}/request-join")
    @Operation(summary = "[바니] 사용자 -> 방 참여 요청 취소", description = "해당 roomId에 보낸 참여 요청을 취소합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._REQUEST_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<String>> cancelRequestToJoin(
        @PathVariable Long roomId, @AuthenticationPrincipal MemberDetails memberDetails) {
        roomCommandService.cancelRequestToJoin(roomId, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 참여 요청 취소 완료"));
    }

    @PatchMapping("/request-join/{requesterId}")
    @Operation(summary = "[바니] 방장 -> 방 참여 요청 수락/거절", description = "requester가 보낸 참여 요청을 수락 또는 거절합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER,
        ErrorStatus._ROOM_ALREADY_EXISTS,
        ErrorStatus._REQUEST_NOT_FOUND,
        ErrorStatus._ROOM_FULL
    })
    public ResponseEntity<ApiResponse<String>> respondToJoinRequest(
        @PathVariable Long requesterId,
        @RequestParam boolean accept, @AuthenticationPrincipal MemberDetails managerDetails) {
        roomCommandService.respondToJoinRequest(requesterId, accept, managerDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(accept ? "참여 요청 수락 완료" : "참여 요청 거절 완료"));
    }

}
