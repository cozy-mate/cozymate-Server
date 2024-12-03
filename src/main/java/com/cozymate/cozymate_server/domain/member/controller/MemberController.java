package com.cozymate.cozymate_server.domain.member.controller;

import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.member.dto.request.SignInRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.request.SignUpRequestDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.MemberDetailResponseDTO;
import com.cozymate.cozymate_server.domain.member.dto.response.SignInResponseDTO;
import com.cozymate.cozymate_server.domain.member.service.MemberCommandService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.code.status.SuccessStatus;

import com.cozymate.cozymate_server.global.utils.SwaggerApiError;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/members")
public class MemberController {

    private final MemberCommandService memberCommandService;

    @PostMapping("/sign-in")
    @Operation(summary = "[말즈] 로그인",
        description = "`request Body : \"client_id\": \"123123\"`<br>"
            + "         *     `\"social_type\": \"KAKAO\"`<br>")
    @SwaggerApiError({
        ErrorStatus._MEMBER_BINDING_FAIL,
    })
    ResponseEntity<ApiResponse<SignInResponseDTO>> signIn(
        @Valid @RequestBody SignInRequestDTO signInRequestDTO
    ) {

        SignInResponseDTO signInResponseDTO = memberCommandService.signIn(signInRequestDTO);

        return ResponseEntity.ok(ApiResponse.onSuccess(signInResponseDTO));
    }

    @GetMapping("/check-nickname")
    @Operation(summary = "[말즈] 닉네임 유효성 검증",
        description = "false : 사용 불가, true : 사용 가능")
    ResponseEntity<ApiResponse<Boolean>> checkNickname(
        @RequestParam @Length(min = 2, max = 10) String nickname) {
        Boolean isValid = memberCommandService.checkNickname(nickname);

        return ResponseEntity.status(SuccessStatus._OK.getHttpStatus())
            .body(ApiResponse.onSuccess(isValid));
    }

    @PostMapping("/sign-up")
    @Operation(summary = "[말즈] 회원가입",
        description = "`request Header : Bearer 임시토큰` <br>"
            + "`request Body : \"name\": \"John Doe\",`<br>"
            + "         *     `\"nickName\": \"johnny\",`<br>"
            + "         *     `\"gender\": \"MALE\",`<br>"
            + "         *     `\"birthday\": \"1990-01-01\"`<br>"
            + "         *     `\"persona\" : 1`")
    @SwaggerApiError({
        ErrorStatus._MEMBER_BINDING_FAIL
    })
    ResponseEntity<ApiResponse<SignInResponseDTO>> signUp(
        @RequestAttribute("client_id") String clientId,
        @RequestBody @Valid SignUpRequestDTO signUpRequestDTO) {

        SignInResponseDTO signInResponseDTO = memberCommandService.signUp(clientId,
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
        MemberDetailResponseDTO memberDetailResponseDTO = memberCommandService.getMemberDetailInfo(
            memberDetails);

        return ResponseEntity.ok(ApiResponse.onSuccess(memberDetailResponseDTO));
    }


    @GetMapping("/sign-out")
    @Operation(summary = "[말즈] 로그아웃",
        description = "사용자를 로그아웃 시킵니다. 스웨거에서는 동작하지 않습니다!")
    @Deprecated
    public void signOut() {
    }


    @PostMapping("/update-nickname")
    @Operation(summary = "[말즈] 사용자 닉네임 수정",
        description = "사용자의 닉네임을 수정합니다.<br>예시: `nickname=말즈`")
    ResponseEntity<ApiResponse<Boolean>> updateNickname(
        @RequestParam String nickname,
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        memberCommandService.updateNickname(memberDetails.member(), nickname);
        return ResponseEntity.ok(ApiResponse.onSuccess(true));
    }

    @PostMapping("/update-persona")
    @Operation(summary = "[말즈] 사용자 프로필 이미지 수정",
        description = "사용자의 프로필 이미지를 수정합니다.<br>1~16 사이의 정수값을 전달하며, 이 값은 사전에 정의된 이미지 ID를 나타냅니다.<br>예시: `persona=3`")
    ResponseEntity<ApiResponse<Boolean>> updatePersona(
        @RequestParam
        @Range(min = 1, max = 16)
        Integer persona,
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        memberCommandService.updatePersona(memberDetails.member(), persona);
        return ResponseEntity.ok(ApiResponse.onSuccess(true));
    }

    @PostMapping("/update-birthday")
    @Operation(summary = "[말즈] 사용자 생일 수정",
        description = "사용자의 생일을 수정합니다.<br>날짜는 'yyyy-MM-dd' 형식으로 전달되어야 합니다.<br>예시: `localDate=2000-01-01`")
    ResponseEntity<ApiResponse<Boolean>> updateBirthday(
        @RequestParam
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate localDate,
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        memberCommandService.updateBirthday(memberDetails.member(), localDate);
        return ResponseEntity.ok(ApiResponse.onSuccess(true));
    }

    @PostMapping("/update-majorName")
    @Operation(summary = "[말즈] 사용자 학과 수정",
        description = "사용자의 학과명을 수정합니다.<br>학과명은 문자열 형식으로 전달됩니다.<br>예시: `majorName=컴퓨터공학과`")
    ResponseEntity<ApiResponse<Boolean>> updateMajorName(
        @RequestParam
        @NotEmpty
        @NotNull
        String majorName,
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        memberCommandService.updateMajor(memberDetails.member(), majorName);
        return ResponseEntity.ok(ApiResponse.onSuccess(true));
    }


    @Operation(summary = "[말즈] 회원 탈퇴 API", description = "현재 로그인한 사용자를 탈퇴시킵니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> withdraw(
        @AuthenticationPrincipal MemberDetails memberDetails) {
        memberCommandService.withdraw(memberDetails);

        return ResponseEntity.ok(ApiResponse.onSuccess("회원 탈퇴가 완료되었습니다."));
    }


}