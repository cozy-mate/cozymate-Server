package com.cozymate.cozymate_server.domain.member.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.member.dto.request.SignUpRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.request.UpdateRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.request.WithdrawRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberUniversityInfoResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;
import com.cozymate.cozymate_server.domain.member.service.MemberService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;

import com.cozymate.cozymate_server.global.utils.BannedWordValid;
import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.validator.constraints.Length;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/members")
@Validated
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/check-nickname")
    @Operation(summary = "[말즈] 닉네임 유효성 검증",
        description = "false : 사용 불가, true : 사용 가능")
    ResponseEntity<ApiResponse<Boolean>> checkNickname(
        @RequestParam @Length(min = 2, max = 10) @BannedWordValid String nickname) {
        Boolean isValid = memberService.checkNickname(nickname);

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
            .body(ApiResponse.onSuccess(isValid));
    }

    @PostMapping("/sign-up")
    @Operation(
        summary = "[말즈] (수정 3.27)회원가입",
        description = "`Request Header` : Bearer 임시토큰 <br>" +
            "`Request Body` 예시: <br>" +
            "<br><code>" +
            "{<br>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"nickname\": \"닉네임\",<br>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"gender\": \"MALE\",<br>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"birthday\": \"2000-01-01\",<br>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"persona\": 1,<br>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;\"memberStatPreferenceDto\": {<br>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"preferenceList\": [<br>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"wakeUpTime\",<br>"
            +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"sleepingTime\",<br>"
            +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"coolingIntensity\",<br>"
            +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\"studying\"<br>"
            +
            "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]<br>" +
            "&nbsp;&nbsp;&nbsp;&nbsp;}<br>" +
            "}" +
            "</code>"
    )

    @SwaggerApiError({
        ErrorStatus._MEMBER_BINDING_FAIL
    })
    ResponseEntity<ApiResponse<SignInResponseDTO>> signUp(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestBody @Valid SignUpRequestDTO signUpRequestDTO) {

        SignInResponseDTO signInResponseDTO = memberService.signUp(memberDetails.member(),
            signUpRequestDTO);

        return ResponseEntity.ok(ApiResponse.onSuccess(signInResponseDTO));
    }


    @GetMapping("/member-info")
    @Operation(summary = "[말즈] 사용자 정보 조회",
        description = "`request Header : Bearer access토큰`")
    @SwaggerApiError({
        ErrorStatus._MEMBER_BINDING_FAIL,
        ErrorStatus._MEMBER_NOT_FOUND
    })
    ResponseEntity<ApiResponse<MemberDetailResponseDTO>> getMemberInfo(
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        MemberDetailResponseDTO memberDetailResponseDTO = memberService.getMemberDetailInfo(
            memberDetails);

        return ResponseEntity.ok(ApiResponse.onSuccess(memberDetailResponseDTO));
    }


    @GetMapping("/sign-out")
    @Operation(summary = "[말즈] 로그아웃",
        description = "사용자를 로그아웃 시킵니다. 스웨거에서는 동작하지 않습니다!")
    @Deprecated
    public void signOut() {
    }

    @PatchMapping("/update")
    @Operation(summary = "[말즈] (수정 2025.3.28) 사용자 정보 수정",
        description = "사용자의 기본정보를 수정합니다."
            + "<br>닉네임 : `nickname=말즈`"
            + "<br>프로필 이미지 : `persona=3`"
            + "<br>생일 : `birthDay=2000-01-01`"
            + "<br>학과명 : `majorName=컴퓨터공학과`")
    ResponseEntity<ApiResponse<Boolean>> update(
        @RequestBody @Valid UpdateRequestDTO requestDTO,
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        memberService.update(memberDetails.member(), requestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess(true));
    }

    @Operation(summary = "[말즈] 회원 탈퇴 API", description = "현재 로그인한 사용자를 탈퇴시킵니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> withdraw(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @Valid WithdrawRequestDTO withdrawRequestDTO) {
        memberService.withdraw(withdrawRequestDTO, memberDetails);

        return ResponseEntity.ok(ApiResponse.onSuccess("회원 탈퇴가 완료되었습니다."));
    }

    @Operation(summary = "[말즈] 인증된 학교 정보 API (신규 2025.5.12)", description = "사용자의 학교이름, 메일주소, 학과")
    @GetMapping("/university-info")
    public ResponseEntity<ApiResponse<MemberUniversityInfoResponseDTO>> getMemberUniversityInfo(
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(memberService.getMemberUniversityInfo(memberDetails.member()))
        );
    }

}