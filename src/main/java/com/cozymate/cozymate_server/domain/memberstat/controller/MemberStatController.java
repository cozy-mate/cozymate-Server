package com.cozymate.cozymate_server.domain.memberstat.controller;

import com.cozymate.cozymate_server.domain.memberstat.converter.MemberStatConverter;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatRequestDTO;
import com.cozymate.cozymate_server.domain.memberstat.dto.MemberStatResponseDTO.MemberStatQueryResponseDTO;
import com.cozymate.cozymate_server.domain.memberstat.service.MemberStatCommandService;
import com.cozymate.cozymate_server.domain.memberstat.service.MemberStatQueryService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/members/stat")
@RequiredArgsConstructor
@RestController
public class MemberStatController {

    private final MemberStatCommandService memberStatCommandService;
    private final MemberStatQueryService memberStatQueryService;

    /**
     * TODO: member는 추후 시큐리티 인증 객체에서 받아오는 것으로 변경 예정, path 변경 예정 사항("/memberId" -> "/")
     */
    @Operation(
        summary = "[포비] 사용자 상세정보 등록",
        description = "Path Variable로 memberId, body로 사용자 상세정보를 넣어 사용합니다.\n\n"
            + "시간은 형식에 맞춰 meridian은 오전, 오후, time은 값을 주시면 됩니다.\n\n"
            + "에어컨, 히터, 예민도들은 모두 정수로 주시면 됩니다.\n\n"
            + "학번의 경우 09학번-> \"09\"로 주시면 됩니다."
    )
    @PostMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Long>> createMemberStat(
        @PathVariable("memberId") Long memberId,@Valid @RequestBody MemberStatRequestDTO.MemberStatCommandRequestDTO memberStatCommandRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(memberStatCommandService.createMemberStat(memberId,memberStatCommandRequestDTO)));
    }

    /**
     * TODO: member는 추후 시큐리티 인증 객체에서 받아오는 것으로 변경 예정, path 변경 예정 사항("/memberId" -> "/")
     */
    @Operation(
        summary = "[포비] 사용자 상세정보 수정",
        description = "Path Variable로 memberId, body로 사용자 상세정보를 넣어 사용합니다.\n\n"
            + "시간은 형식에 맞춰 meridian은 오전, 오후, time은 값을 주시면 됩니다.\n\n"
            + "에어컨, 히터, 예민도들은 모두 정수로 주시면 됩니다.\n\n"
            + "학번의 경우 09학번-> \"09\"로 주시면 됩니다."
    )
    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Long>> modifyMemberStat(
        @PathVariable("memberId") Long memberId,@Valid @RequestBody MemberStatRequestDTO.MemberStatCommandRequestDTO memberStatCommandRequestDTO) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(memberStatCommandService.modifyMemberStat(memberId,memberStatCommandRequestDTO)));
    }

    /**
     * TODO: member는 추후 시큐리티 인증 객체에서 받아오는 것으로 변경 예정, path 변경 예정 사항("/memberId" -> "/")
     */
    @Operation(
        summary = "[포비] 사용자 상세정보 조회",
        description = "Path Variable로 memberId를 넣어 사용합니다.\n\n"
            + "시간 관련 처리를 유의해주세요."
    )
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberStatQueryResponseDTO>> getMemberStat(
        @PathVariable("memberId") Long memberId) {
        MemberStatQueryResponseDTO memberStatQueryResponseDTO = MemberStatConverter.toDto(memberStatQueryService.getMemberStat(memberId));
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                memberStatQueryResponseDTO
            ));
    }

}
