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
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "한글, 영어 및 숫자만 입력해주세요. 특수문자는 불가합니다.")
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
