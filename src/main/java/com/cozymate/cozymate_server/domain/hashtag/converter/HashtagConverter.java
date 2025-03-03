package com.cozymate.cozymate_server.domain.hashtag.converter;

import com.cozymate.cozymate_server.domain.hashtag.Hashtag;

public class HashtagConverter {
    public static Hashtag toHashtag(String tag) {
        return Hashtag.builder()
            .name(tag)
            .build();
    }

}
