package com.cozymate.cozymate_server.domain.member.controller;

import com.cozymate.cozymate_server.domain.auth.utils.MemberDetails;
import com.cozymate.cozymate_server.domain.member.dto.MemberRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;
import com.cozymate.cozymate_server.domain.member.service.MemberService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/check-nickname")
    ApiResponse<Boolean> checkNickname(@RequestParam String nickname) {
        Boolean isValid = memberService.checkNickname(nickname);
        return ApiResponse.onSuccess(isValid);
    }

    @PostMapping("/join")
    ApiResponse<MemberResponseDTO.LoginResponseDTO> join(
            @RequestAttribute("client_id") String clientId,
            @RequestBody @Valid MemberRequestDTO.JoinRequestDTO joinRequestDTO
    ) {
        // todo : 파라미터 바인딩 검증 로직 추가
        MemberResponseDTO.LoginResponseDTO loginResponseDTO = memberService.join(clientId, joinRequestDTO);
        return ApiResponse.onSuccess(loginResponseDTO);
    }

    @GetMapping("/login")
    ApiResponse<MemberResponseDTO.LoginResponseDTO> login(
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        MemberResponseDTO.LoginResponseDTO loginResponseDTO = memberService.login(memberDetails);
        return ApiResponse.onSuccess(loginResponseDTO);
    }

    @GetMapping("/member-info")
    ApiResponse<MemberResponseDTO.MemberInfoDTO> getMemberInfo(
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        MemberResponseDTO.MemberInfoDTO memberInfo = memberService.getMemberInfo(memberDetails);
        return ApiResponse.onSuccess(memberInfo);
    }


}
