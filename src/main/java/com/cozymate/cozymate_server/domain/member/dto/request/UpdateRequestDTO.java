package com.cozymate.cozymate_server.domain.member.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

public record UpdateRequestDTO(
    @NotNull(message = "null일 수 없습니다.")
    @NotEmpty(message = "비어 있을 수 없습니다.")
    @Length(min = 2, max = 8, message = "닉네임 길이는 2~8")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영어, 숫자만 가능합니다.")
    String nickname,

    @NotNull(message = "null일 수 없습니다.")
    @NotEmpty(message = "비어 있을 수 없습니다.")
    String majorName,

    @NotNull(message = "null일 수 없습니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday,

    @NotNull(message = "null일 수 없습니다.")
    @Range(min = 1, max = 16, message = "프로필 캐릭터는 1 ~ 16")
    Integer persona
) {

}
