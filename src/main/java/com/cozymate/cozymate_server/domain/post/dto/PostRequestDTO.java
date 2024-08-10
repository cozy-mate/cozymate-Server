package com.cozymate.cozymate_server.domain.post.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostRequestDTO {

    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private List<String> imageList;

}
