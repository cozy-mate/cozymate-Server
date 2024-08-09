package com.cozymate.cozymate_server.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface SocialLoginController {
    ResponseEntity<?> signIn();
    ResponseEntity<?> callBack(String code, HttpServletRequest request);
}
