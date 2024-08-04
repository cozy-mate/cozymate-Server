package com.cozymate.cozymate_server.domain.member.controller;

import com.cozymate.cozymate_server.domain.auth.utils.MemberDetails;
import com.cozymate.cozymate_server.domain.member.dto.MemberRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO.LoginResponseDTO;
import com.cozymate.cozymate_server.domain.member.service.MemberService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v3")
public class MemberController {
    private final MemberService memberService;

    //todo : nosql로 금지어 추가
    @GetMapping("/check-nickname")
    @Operation(summary = "[말즈] 닉네임 유효성 검증",
            description = "false : 사용 불가, true : 사용 가능")
    ResponseEntity<ApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        Boolean isValid = memberService.checkNickname(nickname);
        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus()).body(ApiResponse.onSuccess(isValid));
    }

    @PostMapping("/join")
    @Operation(summary = "[말즈] 회원가입",
            description = "request Header : Bearer 임시토큰"
                    + "request Body : \"name\": \"John Doe\",\n"
                    + "         *     \"nickName\": \"johnny\",\n"
                    + "         *     \"gender\": \"MALE\",\n"
                    + "         *     \"birthday\": \"1990-01-01\"\n"
                    + "         *     \"persona\" : 1")
    ResponseEntity<ApiResponse<MemberResponseDTO.LoginResponseDTO>> join(
            @RequestAttribute("client_id") String clientId,
            @RequestBody @Valid MemberRequestDTO.JoinRequestDTO joinRequestDTO
    ) {
        // todo : 파라미터 바인딩 검증 로직 추가

        MemberDetails memberDetails = memberService.join(clientId, joinRequestDTO);
        HttpHeaders headers = memberService.getHeader(memberDetails);
        LoginResponseDTO loginResponseDTO = memberService.getBody(memberDetails);

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .headers(headers)
                .body(ApiResponse.onSuccess(loginResponseDTO));
    }

    @GetMapping("/login")
    @Operation(summary = "[말즈] 로그인",
            description = "request Header : Bearer access토큰")
    ResponseEntity<ApiResponse<MemberResponseDTO.LoginResponseDTO>> login(
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        HttpHeaders headers = memberService.getHeader(memberDetails);
        LoginResponseDTO loginResponseDTO = memberService.getBody(memberDetails);
        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
                .headers(headers)
                .body(ApiResponse.onSuccess(loginResponseDTO));
    }

    @GetMapping("/member-info")
    @Operation(summary = "[말즈] 사용자 정보 조회",
            description = "request Header : Bearer access토큰")
    ResponseEntity<ApiResponse<MemberResponseDTO.MemberInfoDTO>> getMemberInfo(
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        MemberResponseDTO.MemberInfoDTO memberInfoDTO = memberService.getMemberInfo(memberDetails);
        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus()).body(ApiResponse.onSuccess(memberInfoDTO));
    }




}
