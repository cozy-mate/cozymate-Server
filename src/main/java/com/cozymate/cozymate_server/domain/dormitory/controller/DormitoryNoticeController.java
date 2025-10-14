package com.cozymate.cozymate_server.domain.dormitory.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.dormitory.dto.response.DormitoryNoticeResponseDTO;
import com.cozymate.cozymate_server.domain.dormitory.service.DormitoryNoticeService;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dormitory")
public class DormitoryNoticeController {

    private final DormitoryNoticeService dormitoryNoticeService;

    @GetMapping("/notice/preview")
    @Operation(summary = "[바니] 공지사항 미리보기 조회", description = "최신 공지 3개를 조회합니다.")
    public ResponseEntity<ApiResponse<List<DormitoryNoticeResponseDTO>>> getPreviewNoticeList(
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(dormitoryNoticeService.getPreviewNoticeList(memberDetails.member()))
        );
    }

    @GetMapping("/notice")
    @Operation(summary = "[바니] 전체 공지사항 조회", description = "공지사항을 최신순으로 조회합니다. isImportant 값에 따라 중요 공지사항(true) 또는 일반 공지사항(false)을 선택할 수 있습니다.")
    public ResponseEntity<ApiResponse<PageResponseDto<List<DormitoryNoticeResponseDTO>>>> getNoticeList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") @PositiveOrZero int page,
        @RequestParam(defaultValue = "10") @Positive int size,
        @RequestParam boolean isImportant
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                dormitoryNoticeService.getNoticeList(memberDetails.member(), page, size, isImportant))
        );
    }

}
