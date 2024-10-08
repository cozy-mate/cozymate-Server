package com.cozymate.cozymate_server.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostUpdateDTO {

    @NotNull
    private Long roomId;

    @NotNull
    private Long postId;

    @NotBlank
    private String content;

    private List<String> imageList;

}

