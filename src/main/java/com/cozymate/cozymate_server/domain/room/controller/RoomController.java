package com.cozymate.cozymate_server.domain.room.controller;


import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.room.dto.CozymateResponse;
import com.cozymate.cozymate_server.domain.room.dto.InviteRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomCreateResponse;
import com.cozymate.cozymate_server.domain.room.dto.RoomJoinResponse;
import com.cozymate.cozymate_server.domain.room.service.RoomCommandService;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
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

    @PostMapping("/create")
    @Operation(summary = "[바니] 방생성 기능", description = "방이름, 프로필이미지, 인원수를 입력합니다.")
    public ResponseEntity<ApiResponse<String>> createRoom(@Valid @RequestBody RoomCreateRequest request,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        // TODO: 시큐리티 이용해 사용자 인증 받아야 함.
        roomCommandService.createRoom(request, memberDetails.getMember());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 생성 완료"));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "[바니] 생성한 방 정보 조회 기능", description = "방 아이디를 입력합니다.")
    public ResponseEntity<ApiResponse<RoomCreateResponse>> getRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        RoomCreateResponse response = roomQueryService.getRoomById(roomId, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "[바니] 방 삭제 기능", description = "해당 roomId의 방을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        roomCommandService.deleteRoom(roomId, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 삭제 완료"));
    }

    @GetMapping("/join")
    @Operation(summary = "[바니] 초대코드로 방 정보 조회 기능", description = "초대코드를 입력하면 방 정보를 조회합니다. (팝업창에 뜰 정보)")
    public ResponseEntity<ApiResponse<RoomJoinResponse>> getRoomInfo(@RequestParam String inviteCode) {
        RoomJoinResponse roomJoinResponse = roomQueryService.getRoomByInviteCode(inviteCode);
        return ResponseEntity.ok(ApiResponse.onSuccess(roomJoinResponse));
    }

    @PostMapping("/{roomId}/join")
    @Operation(summary = "[바니] 방 참여 확인", description = "방에 참여됩니다.")
    public ResponseEntity<ApiResponse<String>> joinRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        roomCommandService.joinRoom(roomId, memberDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 참여 완료"));
    }

    @GetMapping("/available-friends")
    @Operation(summary = "[바니] 방에 초대할 코지메이트 목록 조회", description = "로그인한 멤버의 코지메이트 목록을 불러옵니다.")
    public ResponseEntity<ApiResponse<List<CozymateResponse>>> getCozymateList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(roomQueryService.getCozymateList(memberDetails.getMember().getId())));
    }

    @PostMapping("/{roomId}}/invite")
    @Operation(summary = "[바니] 선택한 코지메이트 방에 초대요청 보내기", description = "해당하는 roomId에 선택한 코지메이트를 초대합니다.")
    public ResponseEntity<ApiResponse<String>> inviteCozymate(@PathVariable Long roomId,
        @RequestBody List<Long> inviteeIdList, @AuthenticationPrincipal MemberDetails inviterDetails) {
        roomCommandService.sendInvitation(roomId, inviteeIdList, inviterDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 초대 요청 완료"));
    }

    @GetMapping ("/{memberId}/request-invites")
    @Operation(summary = "[바니] 방 초대 요청 조회", description = "해당 사용자가 수신한 초대 요청을 조회합니다.")
    public ResponseEntity<ApiResponse<InviteRequest>> getRequestInvites(@AuthenticationPrincipal MemberDetails inviteeDetails) {
        InviteRequest inviteRequest = roomQueryService.getInvitations(inviteeDetails.getMember().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(inviteRequest));
    }


    @PostMapping("/{roomId}/invite-request")
    @Operation(summary = "[바니] 방 초대 요청/수락", description = "해당 roomId에서 온 초대요청을 수락 또는 거절합니다.")
    public ResponseEntity<ApiResponse<String>> getInviteRequest(
        @PathVariable Long roomId, @AuthenticationPrincipal MemberDetails inviteeDetails, @RequestParam boolean accept) {
        roomCommandService.respondToInviteRequest(roomId, inviteeDetails.getMember().getId(), accept);
        return ResponseEntity.ok(ApiResponse.onSuccess("초대 요청에 대한 처리 완료"));
    }

}
