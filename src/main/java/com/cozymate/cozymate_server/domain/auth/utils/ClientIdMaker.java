package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.domain.member.enums.SocialType;

import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/**
 * 소셜로그인 타입과, 소셜 서비스에서 제공한 clientId로 자체 클라이언트 id 를 만드는 util 클래스
 * ex.
 * 카카오 id가 12345, 소셜 타입이 KAKAO 면
 * -> clientId = "12345@KAKAO"
 */
public class ClientIdMaker {
    public static final int MEMBER_ID_INDEX = 0;
    public static final int SOCIAL_TYPE_INDEX = 1;
    public static final String DELIMITER = ":";

    public static String makeClientId(String memberId, SocialType socialType) {
        if(socialType.equals(SocialType.TEST)){
            memberId = generateUUID();
        }
        return memberId + DELIMITER + socialType.toString();
    }
    private static String generateUUID(){
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    }
    public static SocialType getSocialTypeInClientId(String clientId) {
        String socialTypePart = Arrays.asList(clientId.split(DELIMITER)).get(SOCIAL_TYPE_INDEX);
        return SocialType.valueOf(socialTypePart);
    }

    public static String getClientIdAtSocialService(String clientId) {
        return Arrays.asList(clientId.split(DELIMITER)).get(MEMBER_ID_INDEX);
    }

}
