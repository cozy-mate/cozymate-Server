package com.cozymate.cozymate_server.domain.post.dto;

import com.cozymate.cozymate_server.domain.mate.Mate;
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
public class PostSummaryDTO {

    private Long id;
    private Long writerId;
    private String content;
    private String nickname;
    private int persona;
    private LocalDateTime createdAt;
    private List<String> imageList;
    private Integer commentCount;

}
