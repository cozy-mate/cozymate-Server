package com.cozymate.cozymate_server.domain.memberstat_v2.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.memberstat.service.MemberStatCommandService;
import com.cozymate.cozymate_server.domain.memberstat.service.MemberStatQueryService;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.request.CreateMemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatDetailAndRoomIdAndEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatDetailWithMemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatPageResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatRandomListResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.dto.response.MemberStatSearchResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat_v2.service.MemberStatService_v2;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/members/stat/v2")
@RequiredArgsConstructor
@RestController
public class MemberStatController_v2 {


    private final MemberStatService_v2 memberStatService;
    @Operation(
        summary = "[포비] 사용자 상세정보 등록",
        description = "사용자의 토큰을 넣어 사용하고, body로 사용자 상세정보를 넣어 사용합니다.\n\n"
            + "시간은 형식에 맞춰 meridian은 오전, 오후, time은 값을 주시면 됩니다.\n\n"
            + "에어컨, 히터, 예민도들은 모두 정수로 주시면 됩니다.\n\n"
            + "학번의 경우 09학번-> \"09\"로 주시면 됩니다.\n\n"
            + "성격은 다중 선택 변경되었으므로, 배열로 보내주시면 됩니다."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_EXISTS,
        ErrorStatus._UNIVERSITY_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_MERIDIAN_NOT_VALID
    })
    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> createMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody CreateMemberStatRequestDTO createMemberStatRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatService.createMemberStat(memberDetails.member(),createMemberStatRequestDTO)
            ));
    }

}
