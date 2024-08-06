package com.cozymate.cozymate_server.domain.room.controller;

import com.cozymate.cozymate_server.domain.room.dto.RoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomCreateResponse;
import com.cozymate.cozymate_server.domain.room.dto.RoomJoinResponse;
import com.cozymate.cozymate_server.domain.room.service.RoomCommandService;
import com.cozymate.cozymate_server.domain.room.service.RoomQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<String>> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        // TODO: 시큐리티 이용해 사용자 인증 받아야 함.
        roomCommandService.createRoom(request);
        return ResponseEntity.ok(ApiResponse.onSuccess("방 생성 완료"));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "[바니] 생성한 방 정보 조회 기능", description = "방 아이디를 입력합니다.")
    public ResponseEntity<ApiResponse<RoomCreateResponse>> getRoom(@PathVariable Long roomId, @RequestParam Long memberId) {
        RoomCreateResponse response = roomQueryService.getRoomById(roomId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

    @GetMapping("/join")
    @Operation(summary = "[바니] 초대코드로 방 정보 조회 기능", description = "초대코드를 입력하면 방 정보를 조회합니다. (팝업창에 뜰 정보)")
    public ResponseEntity<ApiResponse<RoomJoinResponse>> getRoomInfo(@RequestParam String inviteCode) {
        RoomJoinResponse roomJoinResponse = roomQueryService.getRoomByInviteCode(inviteCode);
        return ResponseEntity.ok(ApiResponse.onSuccess(roomJoinResponse));
    }

    @PostMapping("/{roomId}/join")
    @Operation(summary = "[바니] 방 참여 확인 버튼", description = "방에 참여됩니다.")
    public ResponseEntity<ApiResponse<String>> joinRoom(@PathVariable Long roomId, @RequestBody Long memberId) {
        // TODO: 추후 memberId 수정 예정
        roomCommandService.joinRoom(roomId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess("방 참여 완료"));
    }


}
