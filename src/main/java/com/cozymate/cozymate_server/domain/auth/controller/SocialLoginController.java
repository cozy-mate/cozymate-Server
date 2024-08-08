package com.cozymate.cozymate_server.domain.auth.controller;

import org.springframework.http.ResponseEntity;

public interface SocialLoginController {
    ResponseEntity<?> getUrl();

    ResponseEntity<?> callBack(String code);
}
