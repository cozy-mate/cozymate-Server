package com.cozymate.cozymate_server.domain.auth.service;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO.UrlDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public interface SocialLoginService {

    String CONTENT_TYPE_HEADER_NAME = "Content-type";

    String CONTENT_TYPE_HEADER_VALUE = "application/x-www-form-urlencoded;charset=utf-8";
    UrlDTO getRedirectUrl();

    String getAccessToken(String code);

    String getClientId(String accessToken);
}
