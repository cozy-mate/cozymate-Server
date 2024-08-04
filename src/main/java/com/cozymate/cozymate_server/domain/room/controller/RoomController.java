package com.cozymate.cozymate_server.domain.room.controller;

import com.cozymate.cozymate_server.domain.room.service.RoomCommandService;
import com.cozymate.cozymate_server.domain.room.dto.RoomCreateRequest;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomCommandService roomCommandService;

    @PostMapping("/create")
    @Operation(summary = "[바니] 방생성 기능", description = "방이름, 프로필이미지, 인원수를 입력합니다.")
    public ResponseEntity<ApiResponse<String>> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        // TODO: 시큐리티 이용해 사용자 인증 받아야 함.
        roomCommandService.createRoom(request);
        return ResponseEntity.ok(ApiResponse.onSuccess("방 생성 완료"));
    }

    @DeleteMapping("/{roomId}")
    @Operation(summary = "[바니] 방 삭제 기능", description = "해당 roomId의 방을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Long roomId, Long memberId) {
        // TODO: 시큐리티 이용해 사용자 인증 받아야 함.
        roomCommandService.deleteRoom(roomId, memberId);
        return ResponseEntity.ok(ApiResponse.onSuccess("방 삭제 완료"));
    }

}
