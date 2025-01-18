package com.cozymate.cozymate_server.domain.mail.controller;


import com.cozymate.cozymate_server.domain.auth.userdetails.MemberDetails;
import com.cozymate.cozymate_server.domain.mail.dto.request.MailSendRequestDTO;
import com.cozymate.cozymate_server.domain.mail.dto.request.VerifyRequestDTO;
import com.cozymate.cozymate_server.domain.mail.dto.response.VerifyResponseDTO;
import com.cozymate.cozymate_server.domain.mail.service.MailService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/mail")
@Slf4j
public class MailController {

    private final MailService mailService;

    @PostMapping("")
    @Operation(summary = "[말즈] 메일 보내기 API",
        description = "request body :  <br>"
            + "mailAddress : 12345@inha.edu <br>"
            + "universityId : 1 <br>")
    @Deprecated
    public ResponseEntity<Void> sendUniversityAuthenticationCode(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestBody MailSendRequestDTO sendDTO
    ) {
        mailService.sendUniversityAuthenticationCode(memberDetails, sendDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    @Operation(summary = "[말즈] 메일 인증 API",
        description = "request body :  <br>"
            + " code : a1B2c3 <br>"
            + " universityId : 1 <br>"
            + "response :"
            + " 토큰")
    @Deprecated
    public ResponseEntity<ApiResponse<VerifyResponseDTO>> verifyMemberUniversity(
        @AuthenticationPrincipal MemberDetails memberDetails,
        @RequestBody VerifyRequestDTO verifyDTO

    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(mailService.verifyMemberUniversity(memberDetails, verifyDTO)));
    }

    @GetMapping("/verify")
    @Operation(summary = "[무빗] 메일 인증 여부 반환", description = "메일인증을 받은적이 없거나, 받았는데 인증 확인이 안된 경우 빈 문자열 반환, 메일 인증을 받고, 인증 확인이 된 경우 인증된 메일 주소 반환")
    @Deprecated
    public ResponseEntity<ApiResponse<String>> isVerified(
        @AuthenticationPrincipal MemberDetails memberDetails
    ) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(mailService.isVerified(memberDetails.member())));
    }

    @PostMapping("/test")
    @Operation(summary = "[말즈] 관리자 메일 테스트", description = "관리자에게 메일 보내기 테스트")
    @Deprecated
    public ResponseEntity<ApiResponse<Boolean>> testMail(
    ) {
        log.info("controller 진입 성공");
        mailService.sendCustomMailToAdmin("제목", "내용");
        return ResponseEntity.ok(ApiResponse.onSuccess(true));
    }

}
