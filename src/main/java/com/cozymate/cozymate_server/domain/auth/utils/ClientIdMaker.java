package com.cozymate.cozymate_server.domain.auth.utils;

import com.cozymate.cozymate_server.domain.member.enums.SocialType;

import java.util.Arrays;

public class ClientIdMaker {
    public static final int MEMBER_ID_INDEX = 0;
    public static final int SOCIAL_TYPE_INDEX = 1;
    public static final String DELIMITER = ":";

    public static String makeClientId(String memberId, SocialType socialType) {
        return memberId + DELIMITER + socialType.toString();
    }

    public static SocialType getSocialTypeInClientId(String clientId) {
        String socialTypePart = Arrays.asList(clientId.split(DELIMITER)).get(SOCIAL_TYPE_INDEX);
        return SocialType.valueOf(socialTypePart);
    }

    public static String getClientIdAtSocialService(String clientId) {
        return Arrays.asList(clientId.split(DELIMITER)).get(MEMBER_ID_INDEX);
    }

}
