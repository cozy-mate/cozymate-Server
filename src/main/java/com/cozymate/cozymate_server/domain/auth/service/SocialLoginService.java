package com.cozymate.cozymate_server.domain.auth.service;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO.UrlDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

@Service
public interface SocialLoginService {
    String CONTENT_TYPE_HEADER_NAME = "Content-type";
    String CONTENT_TYPE_HEADER_VALUE = "application/x-www-form-urlencoded;charset=utf-8";
    UrlDTO getRedirectUrl();
    String parseAccessToken(ResponseEntity<String> response);

    HttpEntity<MultiValueMap<String, String>> makeTokenRequest(String code);
    HttpEntity<MultiValueMap<String, String>> makeMemberInfoRequest(String accessToken);

    String getClientId(ResponseEntity<String> response);

}
