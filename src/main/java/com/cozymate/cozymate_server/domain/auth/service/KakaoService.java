package com.cozymate.cozymate_server.domain.auth.service;

import static com.cozymate.cozymate_server.global.response.code.status.ErrorStatus._BAD_REQUEST;

import com.cozymate.cozymate_server.domain.auth.AuthErrorStatus;
import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO.UrlDTO;
import com.cozymate.cozymate_server.domain.auth.utils.ClientIdMaker;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService implements SocialLoginService {


    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private  String KAKAO_REDIRECT_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String AUTHORIZATION_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;


    @Override
    public UrlDTO getRedirectUrl() {
        String url = UriComponentsBuilder.fromHttpUrl(AUTHORIZATION_URI)
                .queryParam("client_id", KAKAO_CLIENT_ID)
                .queryParam("redirect_uri", KAKAO_REDIRECT_URI)
                .queryParam("response_type", "code")
                .build()
                .toString();

        log.info(url);
        return AuthResponseDTO.UrlDTO.builder().redirectUrl(url).build();
    }

    @Override
    public String getAccessToken(String code) {
        // HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE);

        // HTTP Body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", KAKAO_CLIENT_ID);
        body.add("redirect_uri", KAKAO_REDIRECT_URI);
        body.add("code", code);
        body.add("client_secret", KAKAO_CLIENT_SECRET);

        // HTTP 요청 전송
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        String responseBody;

        try {
            RestTemplate rt = new RestTemplate();
            ResponseEntity<String> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
                    kakaoTokenRequest, String.class);
            responseBody = response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // HTTP 요청 에러 처리
            //"Failed to retrieve access token from Kakao: " + e.getMessage(), e
            throw new GeneralException(_BAD_REQUEST);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new GeneralException(_BAD_REQUEST);
        }

        // JSON 응답 파싱
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();
        } catch (JsonProcessingException e) {
            // JSON 파싱 에러 처리
            throw new GeneralException(_BAD_REQUEST);
        }

    }

    public String getClientId(String accessToken) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(USER_INFO_URI, HttpMethod.POST, kakaoUserInfoRequest,
                String.class);

        // responseBody에 있는 정보 꺼내기
        String responseBody = response.getBody();
        String id;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            id = jsonNode.get("id").toString();
        } catch (IOException exception) {
            throw new GeneralException(AuthErrorStatus._KAKAO_ACCESS_TOKEN_PARSING_FAIL);
        }

        return ClientIdMaker.makeClientId(id, SocialType.KAKAO);
    }
}
