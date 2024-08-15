package com.cozymate.cozymate_server.domain.memberstat.controller;

import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.member.Member;
import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityDetailResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatEqualityResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatQueryResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.service.MemberStatCommandService;
import com.cozymate.cozymate_server.domain.memberstat.service.MemberStatQueryService;
import com.cozymate.cozymate_server.global.common.PageResponseDto;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/members/stat")
@RequiredArgsConstructor
@RestController
public class MemberStatController {

    private final MemberStatCommandService memberStatCommandService;
    private final MemberStatQueryService memberStatQueryService;

    @Operation(
        summary = "[포비] 사용자 상세정보 등록",
        description = "사용자의 토큰을 넣어 사용하고, body로 사용자 상세정보를 넣어 사용합니다.\n\n"
            + "시간은 형식에 맞춰 meridian은 오전, 오후, time은 값을 주시면 됩니다.\n\n"
            + "에어컨, 히터, 예민도들은 모두 정수로 주시면 됩니다.\n\n"
            + "학번의 경우 09학번-> \"09\"로 주시면 됩니다."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_EXISTS,
        ErrorStatus._UNIVERSITY_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_MERIDIAN_NOT_VALID
    })
    @PostMapping("")
    public ResponseEntity<ApiResponse<Long>> createMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody MemberStatRequestDTO.MemberStatCommandRequestDTO memberStatCommandRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatCommandService.createMemberStat(memberDetails.getMember(),
                    memberStatCommandRequestDTO)));
    }

    @Operation(
        summary = "[포비] 사용자 상세정보 수정",
        description = "사용자의 토큰을 넣어 사용하고, body로 사용자 상세정보를 넣어 사용합니다.\n\n"
            + "시간은 형식에 맞춰 meridian은 오전, 오후, time은 값을 주시면 됩니다.\n\n"
            + "에어컨, 히터, 예민도들은 모두 정수로 주시면 됩니다.\n\n"
            + "학번의 경우 09학번-> \"09\"로 주시면 됩니다."
    )
    @SwaggerApiError({
        ErrorStatus._UNIVERSITY_NOT_FOUND,
        ErrorStatus._MEMBERSTAT_NOT_EXISTS,
        ErrorStatus._MEMBERSTAT_MERIDIAN_NOT_VALID
    })
    @PutMapping("")
    public ResponseEntity<ApiResponse<Long>> modifyMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid @RequestBody MemberStatRequestDTO.MemberStatCommandRequestDTO memberStatCommandRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatCommandService.modifyMemberStat(memberDetails.getMember(),
                    memberStatCommandRequestDTO)));
    }

    @Operation(
        summary = "[포비] 사용자 상세정보 조회",
        description = "사용자의 토큰을 넣어 사용합니다.\n\n"
            + "시간 관련 처리를 유의해주세요."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    @GetMapping("")
    public ResponseEntity<ApiResponse<MemberStatQueryResponseDTO>> getMemberStat(
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        MemberStatQueryResponseDTO memberStatQueryResponseDTO = MemberStatConverter.toDto(
            memberStatQueryService.getMemberStat(memberDetails.getMember()));
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryResponseDTO
            ));
    }

    @Operation(
        summary = "[포비] 사용자 상세정보 조회",
        description = "사용자 토큰을 넣고, memberId를 PathVariable로 사용합니다.\n\n"
            + "시간 관련 처리를 유의해주세요."
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberStatQueryResponseDTO>> getMemberStat(
        @PathVariable Long memberId
    ) {
        MemberStatQueryResponseDTO memberStatQueryResponseDTO = MemberStatConverter.toDto(
            memberStatQueryService.getMemberStatWithId(memberId));
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryResponseDTO
            ));
    }

    @Operation(
        summary = "[포비] 사용자 상세정보 필터링, 일치율 조회",
        description = "사용자의 토큰을 넣어 사용합니다."
            + "filterList = 필터명1,필터명2,...으로 사용하고, 없을 경우 쿼리문에 아예 filterList를 넣지 않으셔도 됩니다.\n\n"
            + "사용 가능한 필터명(20개):\n"
            + "- acceptance : 합격여부\n"
            + "- admissionYear :  학번\n"
            + "- major : 전공\n"
            + "- numOfRoommate : 신청실\n"
            + "- wakeUpTime : 기상시간\n"
            + "- sleepingTime : 취침시간\n"
            + "- turnOffTime : 소등시간\n"
            + "- smoking : 흡연여부\n"
            + "- sleepingHabit : 잠버릇\n"
            + "- airConditioningIntensity : 에어컨 강도\n"
            + "- heatingIntensity : 히터 강도\n"
            + "- lifePattern : 생활패턴\n"
            + "- intimacy : 친밀도\n"
            + "- canShare : 물건공유\n"
            + "- isPlayGame : 게임여부\n"
            + "- isPhoneCall : 전화여부\n"
            + "- studying : 공부여부\n"
            + "- intake : 섭취여부\n"
            + "- cleanSensitivity : 청결예민도\n"
            + "- noiseSensitivity : 소음예민도\n"
            + "- cleaningFrequency : 청소예민도\n"
            + "- personality : 성격\n"
            + "- mbti : mbti"
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS,
        ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponseDto<List<?>>>> getFilteredMemberList(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) List<String> filterList) {
        Pageable pageable = PageRequest.of(page, 5);
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getMemberStatList(
                    memberDetails.getMember(), filterList, pageable, false)
            ));
    }

    @Operation(
        summary = "[포비] 사용자 상세정보 필터링, 일치율 조회(상세 정보 포함)",
        description = "사용자의 토큰을 넣어 사용합니다."
            + "filterList = 필터명1,필터명2,...으로 사용하고, 없을 경우 쿼리문에 아예 filterList를 넣지 않으셔도 됩니다.\n\n"
            + "사용 가능한 필터명(20개):\n"
            + "- acceptance : 합격여부\n"
            + "- admissionYear :  학번\n"
            + "- major : 전공\n"
            + "- numOfRoommate : 신청실\n"
            + "- wakeUpTime : 기상시간\n"
            + "- sleepingTime : 취침시간\n"
            + "- turnOffTime : 소등시간\n"
            + "- smoking : 흡연여부\n"
            + "- sleepingHabit : 잠버릇\n"
            + "- airConditioningIntensity : 에어컨 강도\n"
            + "- heatingIntensity : 히터 강도\n"
            + "- lifePattern : 생활패턴\n"
            + "- intimacy : 친밀도\n"
            + "- canShare : 물건공유\n"
            + "- isPlayGame : 게임여부\n"
            + "- isPhoneCall : 전화여부\n"
            + "- studying : 공부여부\n"
            + "- intake : 섭취여부\n"
            + "- cleanSensitivity : 청결예민도\n"
            + "- noiseSensitivity : 소음예민도\n"
            + "- cleaningFrequency : 청소예민도\n"
            + "- personality : 성격\n"
            + "- mbti : mbti"
    )
    @SwaggerApiError({
        ErrorStatus._MEMBERSTAT_NOT_EXISTS,
        ErrorStatus._MEMBERSTAT_FILTER_PARAMETER_NOT_VALID
    })
    @GetMapping("/search/details")
    public ResponseEntity<ApiResponse<PageResponseDto<List<?>>>> getFilteredMemberListWithMemberDetails(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) List<String> filterList) {

        Pageable pageable = PageRequest.of(page, 5);

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryService.getMemberStatList(
                    memberDetails.getMember(), filterList, pageable, true)
            ));
    }


}
