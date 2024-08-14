package com.cozymate.cozymate_server.domain.post.dto;

import com.cozymate.cozymate_server.domain.mate.Mate;
import com.cozymate.cozymate_server.domain.postcomment.dto.PostCommentViewDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PostDetailViewDTO {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String nickname;
    private int persona;
    private List<String> imageList;
    private List<PostCommentViewDTO> commentList;
}
