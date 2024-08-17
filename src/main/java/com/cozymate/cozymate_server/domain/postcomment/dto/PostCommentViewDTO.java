package com.cozymate.cozymate_server.domain.postcomment.dto;

import com.cozymate.cozymate_server.domain.mate.Mate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PostCommentViewDTO {

    private Long id;
    private Long writerId;
    private String nickname;
    private int persona;
    private String content;
    private LocalDateTime createdAt;

}
