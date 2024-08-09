package com.cozymate.cozymate_server.domain.auth.service;

import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO;
import com.cozymate.cozymate_server.domain.auth.dto.AuthResponseDTO.UrlDTO;
import com.cozymate.cozymate_server.domain.auth.utils.ClientIdMaker;
import com.cozymate.cozymate_server.domain.member.enums.SocialType;
import com.cozymate.cozymate_server.global.response.code.status.ErrorStatus;
import com.cozymate.cozymate_server.global.response.exception.GeneralException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
    private static final String BODY_ATTRIBUTE_NAME_GRANT_TYPE = "grant_type";
    private static final String BODY_ATTRIBUTE_NAME_CLIENT_SECRET = "client_secret";
    private static final String BODY_ATTRIBUTE_NAME_CODE = "code";
    private static final String BODY_ATTRIBUTE_VALUE_AUTH = "authorization_code";

    private static final String QUERY_PARAMETER_NAME_CLIENT_ID = "client_id";
    private static final String QUERY_PARAMETER_NAME_REDIRECT_URI = "redirect_uri";
    private static final String QUERY_PARAMETER_NAME_RESPONSE_TYPE = "response_type";
    private static final String QUERY_PARAMETER_VALUE_CODE = "code";

    private static final String HEADER_ATTRIBUTE_NAME_AUTH = "Authorization";
    private static final String HEADER_TOKEN_PREFIX = "Bearer ";

    private static final String JSON_ATTRIBUTE_NAME_TOKEN = "access_token";
    private static final String JSON_ATTRIBUTE_NAME_ID = "id";
    private static final String HTTP_ERROR_MESSAGE_FORMAT = "HTTP Error: %s, Status Code: %s, Response Body: %s";
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String USER_INFO_URI;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String TOKEN_URI;
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String AUTHORIZATION_URI;

    @Override
    public UrlDTO getRedirectUrl() {
        String url = UriComponentsBuilder.fromHttpUrl(AUTHORIZATION_URI)
                .queryParam(QUERY_PARAMETER_NAME_CLIENT_ID, KAKAO_CLIENT_ID)
                .queryParam(QUERY_PARAMETER_NAME_REDIRECT_URI, KAKAO_REDIRECT_URI)
                .queryParam(QUERY_PARAMETER_NAME_RESPONSE_TYPE, QUERY_PARAMETER_VALUE_CODE)
                .build()
                .toString();

        log.info(url);
        return AuthResponseDTO.UrlDTO.builder().redirectUrl(url).build();
    }

    @Override
    public String getTokenByCode(String code) {
        HttpEntity<MultiValueMap<String, String>> tokenRequest = makeTokenRequest(code);
        ResponseEntity<String> tokenResponse = getTokenResponse(tokenRequest);

        return parseAccessToken(tokenResponse);
    }

    @Override
    public String getClientIdByToken(String token) {
        HttpEntity<MultiValueMap<String, String>> clientInfoRequest = makeMemberInfoRequest(token);
        ResponseEntity<String> clientInfoResponse = getClientInfoResponse(clientInfoRequest);
        return parseClientId(clientInfoResponse);
    }

    private HttpEntity<MultiValueMap<String, String>> makeTokenRequest(String code) {
        // HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE);

        // HTTP Body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(BODY_ATTRIBUTE_NAME_GRANT_TYPE, BODY_ATTRIBUTE_VALUE_AUTH);
        body.add(QUERY_PARAMETER_NAME_CLIENT_ID, KAKAO_CLIENT_ID);
        body.add(QUERY_PARAMETER_NAME_REDIRECT_URI, KAKAO_REDIRECT_URI);
        body.add(BODY_ATTRIBUTE_NAME_CODE, code);
        body.add(BODY_ATTRIBUTE_NAME_CLIENT_SECRET, KAKAO_CLIENT_SECRET);

        return new HttpEntity<>(body, headers);
    }

    private ResponseEntity<String> getTokenResponse(HttpEntity<MultiValueMap<String, String>> tokenRequest) {
        RestTemplate tokenRt = new RestTemplate();
        try {
            return tokenRt.exchange(TOKEN_URI, HttpMethod.POST,
                    tokenRequest, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String errorMessage = String.format(HTTP_ERROR_MESSAGE_FORMAT,
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(errorMessage);
        }
    }

    private ResponseEntity<String> getClientInfoResponse(HttpEntity<MultiValueMap<String, String>> clientInfoRequest) {
        RestTemplate clientInfoRt = new RestTemplate();
        try {
            return clientInfoRt.exchange(USER_INFO_URI, HttpMethod.POST,
                    clientInfoRequest,
                    String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String errorMessage = String.format(HTTP_ERROR_MESSAGE_FORMAT,
                    e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException(errorMessage);
        }
    }

    private String parseAccessToken(ResponseEntity<String> response) {
        String responseBody = parseResponseBody(response);
        // JSON 응답 파싱
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get(JSON_ATTRIBUTE_NAME_TOKEN).asText();
        } catch (JsonProcessingException e) {
            // JSON 파싱 에러 처리
            throw new GeneralException(ErrorStatus._KAKAO_RESPONSE_PARSING_FAIL);
        }
    }

    private HttpEntity<MultiValueMap<String, String>> makeMemberInfoRequest(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_ATTRIBUTE_NAME_AUTH, HEADER_TOKEN_PREFIX + accessToken);
        headers.add(CONTENT_TYPE_HEADER_NAME, CONTENT_TYPE_HEADER_VALUE);

        return new HttpEntity<>(headers);
    }

    private String parseClientId(ResponseEntity<String> response) {
        String responseBody = parseResponseBody(response);
        String clientId;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            clientId = jsonNode.get(JSON_ATTRIBUTE_NAME_ID).toString();
        } catch (IOException exception) {
            throw new GeneralException(ErrorStatus._KAKAO_ACCESS_TOKEN_PARSING_FAIL);
        }

        return ClientIdMaker.makeClientId(clientId, SocialType.KAKAO);
    }

    private String parseResponseBody(ResponseEntity<String> response) {
        String responseBody;
        try {
            responseBody = response.getBody();
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._KAKAO_RESPONSE_PARSING_FAIL);
        }
        return responseBody;
    }
}
