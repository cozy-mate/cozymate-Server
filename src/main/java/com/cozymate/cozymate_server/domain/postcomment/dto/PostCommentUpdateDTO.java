package com.cozymate.cozymate_server.domain.postcomment.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostCommentUpdateDTO {

    @NotNull
    public Long roomId;
    @NotNull
    public Long postId;
    @NotNull
    public Long commentId;
    @NotBlank
    public String content;

}
