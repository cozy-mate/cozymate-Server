package com.cozymate.cozymate_server.domain.role.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.role.dto.request.CreateRoleRequestDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleDetailResponseDTO;
import com.cozymate.cozymate_server.domain.role.dto.response.RoleIdResponseDTO;
import com.cozymate.cozymate_server.domain.role.service.RoleCommandService;
import com.cozymate.cozymate_server.domain.role.service.RoleQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoleController {

    private final RoleCommandService roleCommandService;
    private final RoleQueryService roleQueryService;


    /**
     * 특정 방에 role 생성
     *
     * @param memberDetails 사용자
     * @param roomId        role 생성할 방 Id
     * @param requestDto    role 데이터
     */
    @PostMapping("/{roomId}/roles")
    @Operation(summary = "[무빗] 특정 방에 role 생성", description = "본인의 룸메라면 Role을 할당할 수 있습니다. mateIdNameList의 객체에서 Id값만 사용합니다. name은 안채워줘도 되긴 함")
    @SwaggerApiError({ErrorStatus._MATE_OR_ROOM_NOT_FOUND})
    public ResponseEntity<ApiResponse<RoleIdResponseDTO>> createRole(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @RequestBody @Valid CreateRoleRequestDTO requestDto
    ) {

        return ResponseEntity.ok(ApiResponse.onSuccess(
            roleCommandService.createRole(memberDetails.member(), roomId, requestDto)
        ));
    }

    /**
     * 특정 방에 role 목록 조회
     *
     * @param memberDetails 사용자
     * @param roomId        role 조회할 방 Id
     * @return role 목록
     */
    @GetMapping("/{roomId}/roles")
    @Operation(summary = "[무빗] 특정 방에 role 목록 조회", description = "")
    @SwaggerApiError({ErrorStatus._MATE_OR_ROOM_NOT_FOUND})
    public ResponseEntity<ApiResponse<List<RoleDetailResponseDTO>>> getRoleList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId
    ) {

        return ResponseEntity.ok(ApiResponse.onSuccess(
            roleQueryService.getRole(memberDetails.member(), roomId)
        ));
    }

    /**
     * 특정 role 삭제
     *
     * @param memberDetails 사용자
     * @param roomId        role 삭제할 방 Id
     * @param roleId        삭제할 role Id
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/{roomId}/roles/{roleId}")
    @Operation(summary = "[무빗] 특정 role 삭제", description = "본인의 룸메라면 Role을 삭제할 수 있습니다.")
    @SwaggerApiError({ErrorStatus._ROLE_NOT_FOUND, ErrorStatus._ROLE_NOT_VALID})
    public ResponseEntity<ApiResponse<String>> deleteRole(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @PathVariable @Positive Long roleId
    ) {
        roleCommandService.deleteRole(memberDetails.member(), roomId, roleId);
        return ResponseEntity.ok(ApiResponse.onSuccess("Role 삭제 완료"));
    }


    /**
     * 특정 role 수정
     *
     * @param memberDetails 사용자
     * @param roomId        사용안함
     * @param roleId        수정할 role Id
     * @param requestDto    수정할 role 데이터
     * @return 수정 완료 메시지
     */
    @PutMapping("/{roomId}/roles/{roleId}")
    @Operation(summary = "[무빗] 특정 role 수정", description = "본인 Role을 수정할 수 있습니다.")
    @SwaggerApiError({ErrorStatus._ROLE_NOT_FOUND, ErrorStatus._ROLE_NOT_VALID})
    public ResponseEntity<ApiResponse<String>> updateRole(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @Positive Long roomId,
        @PathVariable @Positive Long roleId,
        @RequestBody @Valid CreateRoleRequestDTO requestDto
    ) {
        roleCommandService.updateRole(memberDetails.member(), roomId, roleId, requestDto);
        return ResponseEntity.ok(ApiResponse.onSuccess("Role 수정 완료"));
    }

}
