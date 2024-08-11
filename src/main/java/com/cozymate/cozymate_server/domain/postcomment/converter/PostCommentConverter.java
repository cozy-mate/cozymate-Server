package com.cozymate.cozymate_server.domain.postcomment.converter;

import com.cozymate.cozymate_server.domain.postcomment.PostComment;
import com.cozymate.cozymate_server.domain.postcomment.dto.PostCommentViewDTO;

public class PostCommentConverter {

    public static PostCommentViewDTO toDto(PostComment postComment){
        return PostCommentViewDTO.builder()
            .id(postComment.getId())
            .commenter(postComment.getCommenter())
            .content(postComment.getContent())
            .build();
    }

}
