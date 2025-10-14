package com.cozymate.cozymate_server.domain.dormitory.controller;

import com.cozymate.cozymate_server.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.dormitory.dto.response.DormitoryMenuResponseDTO;
import com.cozymate.cozymate_server.domain.dormitory.service.DormitoryMenuService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dormitory")
public class DormitoryMenuController {

    private final DormitoryMenuService dormitoryMenuService;

    @GetMapping("/menu/{date}")
    @Operation(summary = "[바니] 기숙사 식단 조회", description = "입력한 날짜의 기숙사 식단을 조회합니다. \n date=YYYY-MM-DD 형태 ")
    @SwaggerApiError({
        ErrorStatus._DORMITORY_MENU_NOT_FOUND
    })
    public ResponseEntity<ApiResponse<DormitoryMenuResponseDTO>> getMenuByDate(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DormitoryMenuResponseDTO response = dormitoryMenuService.getMenuByDate(memberDetails.member(), date);
        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }

}
