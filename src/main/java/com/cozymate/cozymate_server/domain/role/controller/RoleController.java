package com.cozymate.cozymate_server.domain.role.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.role.dto.RoleRequestDto.CreateRoleRequestDto;
import com.cozymate.cozymate_server.domain.role.dto.RoleResponseDto.RoleListDetailResponseDto;
import com.cozymate.cozymate_server.domain.role.service.RoleCommandService;
import com.cozymate.cozymate_server.domain.role.service.RoleQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
@RequestMapping("/role")
public class RoleController {

    private final RoleCommandService roleCommandService;
    private final RoleQueryService roleQueryService;

    @PostMapping("/{roomId}")
    @Operation(summary = "[무빗] 특정 방에 role 생성", description = "본인의 룸메라면 Role을 할당할 수 있습니다.")
    @SwaggerApiError({ErrorStatus._MATE_NOT_FOUND})
    public ResponseEntity<ApiResponse<String>> createRole(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody CreateRoleRequestDto createRoleRequestDto,
        @PathVariable Long roomId
    ) {
        roleCommandService.createRole(createRoleRequestDto, roomId, memberDetails.getMember());
        return ResponseEntity.ok(ApiResponse.onSuccess("Role 생성 완료."));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "[무빗] 특정 방에 role 목록 조회", description = "")
    @SwaggerApiError({ErrorStatus._MATE_NOT_FOUND})
    public ResponseEntity<ApiResponse<RoleListDetailResponseDto>> getRoleList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId
    ) {

        return ResponseEntity.ok(ApiResponse.onSuccess(
            roleQueryService.getRole(roomId, memberDetails.getMember())
        ));
    }

    @DeleteMapping("{roomId}")
    @Operation(summary = "[무빗] 특정 role 삭제", description = "본인의 룸메라면 Role을 삭제할 수 있습니다.")
    @SwaggerApiError({ErrorStatus._MATE_NOT_FOUND, ErrorStatus._ROLE_NOT_FOUND})
    public ResponseEntity<ApiResponse<String>> deleteRole(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable Long roomId,
        @RequestParam Long roleId
    ) {
        roleCommandService.deleteRole(roomId, roleId, memberDetails.getMember());
        return ResponseEntity.ok(ApiResponse.onSuccess("Role 삭제 완료"));
    }

}
