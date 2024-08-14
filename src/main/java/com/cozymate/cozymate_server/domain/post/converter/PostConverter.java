package com.cozymate.cozymate_server.domain.post.converter;

import com.cozymate.cozymate_server.domain.feed.Feed;
import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.post.Post;
import com.cozymate.cozymate_server.domain.post.dto.PostCreateDTO;
import com.cozymate.cozymate_server.domain.post.dto.PostDetailViewDTO;
import com.cozymate.cozymate_server.domain.post.dto.PostSummaryDTO;
import com.cozymate.cozymate_server.domain.postcomment.PostComment;
import com.cozymate.cozymate_server.domain.postcomment.converter.PostCommentConverter;
import com.cozymate.cozymate_server.domain.postimage.PostImage;
import com.cozymate.cozymate_server.global.utils.ImageUtil;
import java.util.List;

public class PostConverter {

    public static Post toEntity(PostCreateDTO postCreateDTO, Feed feed, Mate writer) {
        return Post.builder()
            .title(postCreateDTO.getTitle())
            .content(postCreateDTO.getContent())
            .feed(feed)
            .writer(writer)
            .build();
    }

    public static PostDetailViewDTO toDto(Post post, List<PostImage> postImage,
        List<PostComment> postComment) {
        return PostDetailViewDTO.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .nickname(post.getWriter().getMember().getNickname())
            .persona(post.getWriter().getMember().getPersona())
            .createdAt(post.getCreatedAt())
            .imageList(
                postImage.stream().map(PostImage::getContent).map(ImageUtil::generateUrl).toList())
            .commentList(postComment.stream().map(PostCommentConverter::toDto).toList())
            .build();
    }

    public static PostSummaryDTO toDto(Post post,
        List<PostImage> postImages,
        Integer commentCount){
        return PostSummaryDTO.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .nickname(post.getWriter().getMember().getNickname())
            .persona(post.getWriter().getMember().getPersona())
            .createdAt(post.getCreatedAt())
            .imageList(
                postImages.stream().map(PostImage::getContent).map(ImageUtil::generateUrl).toList())
            .commentCount(commentCount)
            .build();
    }

}
