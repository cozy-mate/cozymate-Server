package com.cozymate.cozymate_server.domain.room;

import com.cozymate.cozymate_server.domain.room.dto.RoomCreateRequest;
import com.cozymate.cozymate_server.domain.room.dto.RoomCreateResponse;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    /**
     * [POST] 방 생성
     */
    @PostMapping("/create")
    @Operation(summary = "[바니] 방생성 기능", description = "방이름, 프로필이미지, 인원수를 입력합니다.")
    public ResponseEntity<ApiResponse<RoomCreateResponse>> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        // TODO: 시큐리티 이용해 사용자 인증 받아야 함.
        Room room = roomService.createRoom(request);
        RoomCreateResponse response = new RoomCreateResponse(room.getId(), room.getProfileImage(), room.getInviteCode());
        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
            .body(ApiResponse.onSuccess(response));
    }

}
