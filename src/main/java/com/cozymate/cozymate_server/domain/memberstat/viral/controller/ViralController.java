package com.cozymate.cozymate_server.domain.memberstat.viral.controller;

import com.cozymate.cozymate_server.domain.memberstat.viral.dto.CreateMemberStatSnapshotRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.viral.dto.CreateViralSnapshotDTO;
import com.cozymate.cozymate_server.domain.memberstat.viral.dto.LifestyleSnapshotResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.viral.service.MemberStatSnapshotService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/viral")
@Validated
public class ViralController {

    private final MemberStatSnapshotService snapshotService;

    @PostMapping("/create")
    @Operation(summary = "[말즈] 바이럴 테스트용 API",
        description = "dto에 맞춰서 API를 호출해주세요.\n\n"
            + "`viralCode`는 없으면 넣지 말고 호출 해주세요\n\n"
            + "아주 특수한 경우(하루에 바이럴 사용자가 10만건 이상) 생성이 안됩니다.\n\n"
            + "`viralCode`가 잘못된 경우엔 `viralCode`가 없는 경우랑 똑같이 처리됩니다."
    )
    @SwaggerApiError({
        ErrorStatus._VIRAL_CODE_GENERATING_EXCESS,
        ErrorStatus._VIRAL_CODE_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<CreateViralSnapshotDTO>> createViralSnapshotDTO(
        @Valid @RequestBody CreateMemberStatSnapshotRequestDTO requestDTO,
        @RequestParam(name = "viralCode", required = false) String sharerViralCode
    ) {
        CreateViralSnapshotDTO createdDto;
        if (sharerViralCode != null && !sharerViralCode.isBlank()) {
            createdDto = snapshotService.createAndCompare(requestDTO, sharerViralCode);
        } else {
            createdDto = snapshotService.create(requestDTO);
        }
        return ResponseEntity.ok(
            ApiResponse.onSuccess(createdDto)
        );
    }

    @GetMapping("/count")
    @Operation(summary = "[말즈] 바이럴 테스트 참여 수 API")
    @SwaggerApiError({})
    public ResponseEntity<ApiResponse<Long>> getNumberOfParticipants() {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                snapshotService.getNumberOfViralSnapshots()
            )
        );
    }

    @GetMapping("/lifestyle-snapshot")
    @Operation(summary = "[말즈] 바이럴 테스트 snapshot 조회 API")
    @SwaggerApiError({})
    public ResponseEntity<ApiResponse<LifestyleSnapshotResponseDTO>> getLifestyleSnapshot(
        @RequestParam(name = "viralCode") String viralCode
    ) {

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                snapshotService.findLifestyleSnapshot(viralCode)
            )
        );
    }
}
