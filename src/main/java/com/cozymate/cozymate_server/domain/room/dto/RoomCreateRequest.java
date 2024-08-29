package com.cozymate.cozymate_server.domain.room.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomCreateRequest {

    @NotBlank
    @Size(max=12)
    @Pattern(regexp = "^(?!\\s)[가-힣a-zA-Z0-9\\s]+(?<!\\s)$", message = "한글, 영어, 숫자 및 공백만 입력해주세요. 단, 공백은 처음이나 끝에 올 수 없습니다.")
    private String name;
    @NotNull
    @Min(0)
    @Max(15)
    private Integer profileImage;
    @NotNull
    @Min(2)
    @Max(6)
    private Integer maxMateNum;

}
