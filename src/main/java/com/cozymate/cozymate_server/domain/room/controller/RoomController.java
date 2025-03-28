package com.cozymate.cozymate_server.domain.room.controller;


import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.mate.enums.EntryStatus;
import com.cozymate.cozymate_server.domain.room.dto.request.PrivateRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.PublicRoomCreateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.request.RoomUpdateRequestDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.InvitedRoomResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.MateDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomDetailResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomExistResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomIdResponseDTO;
import com.cozymate.cozymate_server.domain.room.dto.response.RoomSearchResponseDTO;
import com.cozymate.cozymate_server.domain.room.service.RoomCommandService;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
    public ResponseEntity<ApiResponse<RoomDetailResponseDTO>> createPrivateRoom(@Valid @RequestBody PrivateRoomCreateRequestDTO request,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        RoomDetailResponseDTO response = roomCommandService.createPrivateRoom(request, memberDetails.member());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PostMapping("/create-public")
    @Operation(summary = "[바니]공개 방 생성 기능", description = "방이름, 프로필이미지, 인원수, 해시태그(1-3개)를 입력합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_ALREADY_EXISTS,
        ErrorStatus._DUPLICATE_HASHTAGS
    })
    public ResponseEntity<ApiResponse<RoomDetailResponseDTO>> createPublicRoom(
        @Valid @RequestBody PublicRoomCreateRequestDTO request,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        RoomDetailResponseDTO response = roomCommandService.createPublicRoom(request, memberDetails.member());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "[바니] 방 정보 조회 기능", description = "방 아이디를 입력합니다. 공개방만 모두 조회 가능하고, 비공개 방은 사용자가 속한 방만 조회 가능합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE
    })
    public ResponseEntity<ApiResponse<RoomDetailResponseDTO>> getRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        RoomDetailResponseDTO response = roomQueryService.getRoomById(roomId, memberDetails.member().getId());
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
        roomCommandService.deleteRoom(roomId, memberDetails.member().getId());
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
        roomCommandService.quitRoom(roomId, memberDetails.member().getId());
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
    public ResponseEntity<ApiResponse<RoomDetailResponseDTO>> getRoomInfo(@RequestParam String inviteCode,
        @AuthenticationPrincipal MemberDetails memberDetails) {
        RoomDetailResponseDTO roomJoinResponse = roomQueryService.getRoomByInviteCode(inviteCode, memberDetails.member().getId());
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
        roomCommandService.joinRoom(roomId, memberDetails.member());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 참여 완료"));
    }

    @GetMapping("/check-roomname")
    @Operation(summary = "[바니] 방 이름 중복 검증", description = "가능하면 true가, 중복시 false가 리턴됩니다.")
    ResponseEntity<ApiResponse<Boolean>> checkRoomName(@RequestParam String roomName) {
        Boolean isValid = roomCommandService.checkRoomName(roomName);
        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus()).body(ApiResponse.onSuccess(isValid));
    }


    @PostMapping("/invite/{inviteeId}")
    @Operation(summary = "[바니] 방장 -> 내방으로 초대하기", description = "방장이 속해있는 roomId에 선택한 코지메이트를 초대합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER,
        ErrorStatus._ROOM_FULL,
        ErrorStatus._INVITATION_ALREADY_SENT,
        ErrorStatus._ROOM_ALREADY_JOINED,
        ErrorStatus._ROOM_ALREADY_EXISTS
    })
    public ResponseEntity<ApiResponse<String>> inviteCozymate(
        @PathVariable Long inviteeId, @AuthenticationPrincipal MemberDetails inviterDetails) {
        roomCommandService.sendInvitation(inviteeId, inviterDetails.member());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 초대 요청 완료"));
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
        roomCommandService.respondToInvitation(roomId, inviteeDetails.member(), accept);
        return ResponseEntity.ok(ApiResponse.onSuccess(accept ? "초대 요청 수락 완료" : "초대 요청 거절 완료"));
    }

    @GetMapping("/exist")
    @Operation(
        summary = "[바니] 로그인한 사용자가 참여한 방이 있는지 여부 조회(수정 - 25.03.28)",
        description = """
        로그인한 사용자가 현재 참여 중인 방이 있는 경우, 해당 roomId와 isRoomManager 값을 반환합니다.
        참여 중인 방이 없을 경우, roomId는 0으로, isRoomManager는 false로 반환됩니다.
        """
    )
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<RoomExistResponseDTO>> getExistRoom(@AuthenticationPrincipal MemberDetails memberDetails) {
        RoomExistResponseDTO response = roomQueryService.getRoomExistInfo(memberDetails.member().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/exist/{memberId}")
    @Operation(summary = "[바니] 다른 사용자가 참여한 방이 있는지 여부 조회", description = "현재 참여중인 방이 있다면 해당 roomId가, 없다면 roomId값이 0으로 리턴됩니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<RoomIdResponseDTO>> getExistRoom(
        @PathVariable Long memberId, @AuthenticationPrincipal MemberDetails memberDetails) {
        RoomIdResponseDTO response = roomQueryService.getExistRoom(memberId, memberDetails.member().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @PatchMapping("/{roomId}")
    @Operation(summary = "[바니] 방 정보 수정", description = "해당 roomId의 정보를 수정합니다. (초대방은 방 이름만, 공개방은 방이름/해시태그 수정)")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE,
        ErrorStatus._NOT_ROOM_MANAGER
    })
    public ResponseEntity<ApiResponse<RoomDetailResponseDTO>> updateRoom(@PathVariable Long roomId,
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody RoomUpdateRequestDTO request) {
        RoomDetailResponseDTO response = roomCommandService.updateRoom(roomId, memberDetails.member().getId(), request);
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
        roomCommandService.forceQuitRoom(roomId, memberId, memberDetails.member());
        return ResponseEntity.ok(ApiResponse.onSuccess("강제 퇴장 완료"));
    }

    @DeleteMapping("/invitee/{inviteeId}")
    @Operation(summary = "[바니] 방장 -> 내방으로 초대 취소 기능", description = "내방으로 초대했던 inviteeId를 이용해 초대를 취소합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER,
        ErrorStatus._INVITATION_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<String>> cancelInvitation(
        @PathVariable Long inviteeId,
        @AuthenticationPrincipal MemberDetails inviterDetails) {
        roomCommandService.cancelInvitation(inviteeId, inviterDetails.member());
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
        roomCommandService.requestToJoin(roomId, memberDetails.member());
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
        roomCommandService.cancelRequestToJoin(roomId, memberDetails.member());
        return ResponseEntity.ok(ApiResponse.onSuccess("방 참여 요청 취소 완료"));
    }

    @PatchMapping("/request-join/{requesterId}")
    @Operation(summary = "[바니] 방장 -> 방 참여 요청 수락/거절", description = "requester가 보낸 참여 요청을 수락 또는 거절합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER,
        ErrorStatus._ROOM_ALREADY_EXISTS,
        ErrorStatus._REQUEST_NOT_FOUND,
        ErrorStatus._ROOM_FULL
    })
    public ResponseEntity<ApiResponse<String>> respondToJoinRequest(
        @PathVariable Long requesterId,
        @RequestParam boolean accept, @AuthenticationPrincipal MemberDetails managerDetails) {
        roomCommandService.respondToJoinRequest(requesterId, accept, managerDetails.member().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess(accept ? "참여 요청 수락 완료" : "참여 요청 거절 완료"));
    }

    @GetMapping("/{roomId}/invited-members")
    @Operation(summary = "[바니] 우리방으로 초대한 멤버 목록 조회", description = "해당 roomId에 초대받은 멤버들을 조회합니다")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE
    })
    public ResponseEntity<ApiResponse<List<MateDetailResponseDTO>>> getInvitedMemberList(
        @PathVariable Long roomId, @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(roomQueryService.getInvitedMemberList(roomId, memberDetails.member().getId())));
    }

    @GetMapping("/requested")
    @Operation(summary = "[바니] 사용자가 참여 요청한 방 목록 조회(수정 - 25.03.28)", description = "로그인한 사용자가 참여 요청한 방 목록을 조회합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<PageResponseDto<List<RoomDetailResponseDTO>>>> getRequestedRoomList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size
        ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(roomQueryService.getRequestedRoomList(memberDetails.member().getId(), page, size)));
    }

    @GetMapping("/invited")
    @Operation(summary = "[바니] 사용자가 초대 요청받은 방 목록 조회", description = "로그인한 사용자가 초대 요청 받은 방 목록을 조회합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<InvitedRoomResponseDTO>> getInvitedRoomList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(roomQueryService.getInvitedRoomList(memberDetails.member().getId())));
    }

    @GetMapping("/pending-members")
    @Operation(summary = "[바니] 방장에게 보이는 방 참여 요청 목록 조회", description = "방장에게 도착한 방 참여 요청을 조회합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER
    })
    public ResponseEntity<ApiResponse<List<MateDetailResponseDTO>>> getPendingMemberList(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(roomQueryService.getPendingMemberList(memberDetails.member().getId())));
    }

    @PatchMapping("/{roomId}/to-public")
    @Operation(summary = "[바니] 공개방으로 전환", description = "roomId에 해당하는 방을 공개방으로 전환합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE,
        ErrorStatus._NOT_ROOM_MANAGER,
        ErrorStatus._PUBLIC_ROOM,
    })
    public ResponseEntity<ApiResponse<String>> convertToPublicRoom(
        @PathVariable Long roomId, @AuthenticationPrincipal MemberDetails memberDetails) {
        roomCommandService.changeToPublicRoom(roomId, memberDetails.member());
        return ResponseEntity.ok(ApiResponse.onSuccess("공개방 전환 완료"));
    }

    @PatchMapping("/{roomId}/to-private")
    @Operation(summary = "[바니] 비공개방으로 전환", description = "roomId에 해당하는 방을 비공개방으로 전환합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MATE,
        ErrorStatus._NOT_ROOM_MANAGER,
        ErrorStatus._PRIVATE_ROOM,
    })
    public ResponseEntity<ApiResponse<String>> convertToPrivateRoom(
        @PathVariable Long roomId, @AuthenticationPrincipal MemberDetails memberDetails) {
        roomCommandService.changeToPrivateRoom(roomId, memberDetails.member().getId());
        return ResponseEntity.ok(ApiResponse.onSuccess("비공개방 전환 완료"));
    }

    @GetMapping("/search")
    @Operation(summary = "[바니] 방 검색", description = "공개방을 검색합니다. 라이프 스타일 없는 경우 가나다순, 있는 경우 평균 일치율 순으로 정렬됩니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<List<RoomSearchResponseDTO>>> searchRooms(
        @RequestParam String keyword, @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(roomQueryService.searchRooms(keyword, memberDetails.member())));
    }

    @GetMapping("/invited-status/{memberId}")
    @Operation(summary = "[바니] 방장 -> 방장이 초대한 사용자인지 조회", description = "방장이 memberId에 해당하는 사용자에게 방 참여 요청을 보냈는지 여부를 조회합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER
    })
    public ResponseEntity<ApiResponse<Boolean>> isInvitedMember(
        @PathVariable Long memberId, @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(roomQueryService.isMemberInEntryStatus(memberId, memberDetails.member(), EntryStatus.INVITED)));
    }

    @GetMapping("pending-status/{memberId}")
    @Operation(summary = "[바니] 방장 -> 방에 참여 요청한 사용자인지 조회", description = "memberId에 해당하는 사용자가 방장의 방에 참여 요청을 보냈는지 여부를 조회합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
        ErrorStatus._ROOM_MANAGER_NOT_FOUND,
        ErrorStatus._NOT_ROOM_MANAGER
    })
    public ResponseEntity<ApiResponse<Boolean>> isPendingMember(
        @PathVariable Long memberId, @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(roomQueryService.isMemberInEntryStatus(memberId, memberDetails.member(), EntryStatus.PENDING)));
    }

    @GetMapping("/{roomId}/invited-status")
    @Operation(summary = "[바니] 사용자 -> 사용자가 초대받은 방인지 조회", description = "로그인한 사용자가 해당 roomId의 방에 초대받았는지 여부를 조회합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND,
    })
    public ResponseEntity<ApiResponse<Boolean>> isInvitedToRoom(
        @PathVariable Long roomId, @AuthenticationPrincipal MemberDetails memberDetails) {
        return ResponseEntity.ok(ApiResponse.onSuccess(roomQueryService.isEntryStatusToRoom(roomId, memberDetails.member().getId(), EntryStatus.INVITED)));
    }

    @GetMapping("/{roomId}/pending-status")
    @Operation(summary = "[바니] 사용자 -> 사용자가 참여 요청한 방인지 조회", description = "로그인한 사용자가 roomId에 해당하는 방에 참여 요청을 했는지 여부를 조회합니다.")
    @SwaggerApiError({
        ErrorStatus._MEMBER_NOT_FOUND,
        ErrorStatus._ROOM_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<Boolean>> isPendingToRoom(
        @PathVariable Long roomId, @AuthenticationPrincipal MemberDetails memberDetails) {
        Boolean response = roomQueryService.isEntryStatusToRoom(roomId, memberDetails.member().getId(), EntryStatus.PENDING);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

}
