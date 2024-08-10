package com.cozymate.cozymate_server.domain.member.controller;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.member.dto.MemberRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.MemberResponseDTO;
import com.cozymate.cozymate_server.domain.member.service.MemberCommandService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v3/member")
public class MemberController {
    private final MemberCommandService memberCommandService;

    //todo : nosql로 금지어 추가
    @GetMapping("/check-nickname")
    @Operation(summary = "[말즈] 닉네임 유효성 검증",
            description = "false : 사용 불가, true : 사용 가능")
    ResponseEntity<ApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        Boolean isValid = memberCommandService.checkNickname(nickname);

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus()).body(ApiResponse.onSuccess(isValid));
    }

    @PostMapping("/sign-up")
    @Operation(summary = "[말즈] 회원가입",
            description = "request Header : Bearer 임시토큰"
                    + "request Body : \"name\": \"John Doe\",\n"
                    + "         *     \"nickName\": \"johnny\",\n"
                    + "         *     \"gender\": \"MALE\",\n"
                    + "         *     \"birthday\": \"1990-01-01\"\n"
                    + "         *     \"persona\" : 1")
    ResponseEntity<ApiResponse<AuthResponseDTO.TokenResponseDTO>> signUp(
            @RequestAttribute("client_id") String clientId,
            @RequestBody @Valid MemberRequestDTO.JoinRequestDTO joinRequestDTO,
            BindingResult bindingResult
    ) {
        log.info("enter MemberController : [post] /member/sign-up");
        if (bindingResult.hasErrors()) {
            throw new GeneralException(ErrorStatus._MEMBER_BINDING_FAIL);
        }
        MemberDetails memberDetails = memberCommandService.join(clientId, joinRequestDTO);

        AuthResponseDTO.TokenResponseDTO tokenResponseDTO = memberCommandService.generateTokenDTO(memberDetails);

        return ResponseEntity.ok(ApiResponse.onSuccess(tokenResponseDTO));
    }

    @GetMapping("/reissue")
    @Operation(summary = "[말즈] 토큰 재발행",
            description = "request Header : Bearer refreshToken")
    ResponseEntity<ApiResponse<AuthResponseDTO.TokenResponseDTO>> reissue(
            @RequestHeader(value = "Refresh") String refreshToken
    ) {
        MemberDetails memberDetails = memberCommandService.extractMemberDetailsByRefreshToken(refreshToken);

        AuthResponseDTO.TokenResponseDTO tokenResponseDTO = memberCommandService.generateTokenDTO(memberDetails);

        return ResponseEntity.ok(ApiResponse.onSuccess(tokenResponseDTO));
    }

    @GetMapping("/member-info")
    @Operation(summary = "[말즈] 사용자 정보 조회",
            description = "request Header : Bearer access토큰")
    ResponseEntity<ApiResponse<MemberResponseDTO.MemberInfoDTO>> getMemberInfo(
            @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        MemberResponseDTO.MemberInfoDTO memberInfoDTO = memberCommandService.getMemberInfo(memberDetails);

        return ResponseEntity.ok(ApiResponse.onSuccess(memberInfoDTO));
    }

    @Operation(summary = "[말즈] 로그아웃",
            description = "사용자를 로그아웃 시킵니다. 스웨거에서는 동작하지 않습니다!")
    @GetMapping("/sign-out")
    public void signOut() {
    }

    @Operation(summary = "[말즈] 회원 탈퇴 API", description = "현재 로그인한 사용자를 탈퇴시킵니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> withdraw(@AuthenticationPrincipal MemberDetails memberDetails) {
        memberCommandService.withdraw(memberDetails);

        return ResponseEntity.ok(ApiResponse.onSuccess("회원 탈퇴가 완료되었습니다."));
    }


}