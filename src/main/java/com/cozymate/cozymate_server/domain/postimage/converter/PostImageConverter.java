package com.cozymate.cozymate_server.domain.postimage.converter;

import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.postimage.PostImage;

public class PostImageConverter {

    public static PostImage toEntity(Post post, String content){
        return PostImage.builder()
            .post(post)
            .content(content)
            .build();
    }

}
