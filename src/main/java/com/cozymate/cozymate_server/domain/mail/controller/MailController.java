package com.cozymate.cozymate_server.domain.mail.controller;


import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.userDetails.MemberDetails;
import com.cozymate.cozymate_server.domain.mail.dto.MailResponse;
import com.cozymate.cozymate_server.domain.mail.service.MailService;
import com.cozymate.cozymate_server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/mail")
public class MailController {
    private final MailService mailService;

    @PostMapping("/{mail-address}")
    public ResponseEntity sendUniversityAuthenticationCode(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable("mail-address") String mailAddress
    ) {
        mailService.sendUniversityAuthenticationCode(memberDetails, mailAddress);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify/{code}")
    public ResponseEntity<ApiResponse<AuthResponseDTO.TokenResponseDTO>> verifyAuthenticationCode(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable("code") String code

    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(mailService.verifyAuthenticationCode(memberDetails, code)));
    }

}
