package com.cozymate.cozymate_server.domain.postcomment.converter;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.postcomment.PostComment;
import com.cozymate.cozymate_server.domain.postcomment.dto.PostCommentCreateDTO;
import com.cozymate.cozymate_server.domain.postcomment.dto.PostCommentViewDTO;

public class PostCommentConverter {

    public static PostCommentViewDTO toDto(PostComment postComment){
        return PostCommentViewDTO.builder()
            .id(postComment.getId())
            .writerId(postComment.getCommenter().getId())
            .nickname(postComment.getCommenter().getMember().getNickname())
            .persona(postComment.getCommenter().getMember().getPersona())
            .content(postComment.getContent())
            .createdAt(postComment.getCreatedAt())
            .build();
    }

    public static PostComment toEntity(PostCommentCreateDTO postCommentCreateDTO, Post post, Mate commenter){
        return PostComment.builder()
            .post(post)
            .commenter(commenter)
            .content(postCommentCreateDTO.getContent())
            .build();
    }

}
