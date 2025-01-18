package com.cozymate.cozymate_server.domain.member.dto.request;

import com.cozymate.cozymate_server.domain.member.enums.Gender;
import com.cozymate.cozymate_server.global.utils.EnumValid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;


/**
 * { "nickname": "말즈", "gender": "MALE", "birthday": "2000-01-20", "persona" : 1, "university : 1 }
 */

@Builder
public record SignUpRequestDTO(
    @NotNull(message = "null일 수 없습니다.")
    @NotEmpty(message = "비어 있을 수 없습니다.")
    @Length(min = 2, max = 8, message = "닉네임 길이는 2~8")
    String nickname,

    @NotNull(message = "null일 수 없습니다.")
    @EnumValid(enumClass = Gender.class)
    String gender,

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "null일 수 없습니다.")
    LocalDate birthday,

    @NotNull(message = "null일 수 없습니다.")
    @Range(min = 1, max = 16, message = "프로필 캐릭터는 1 ~ 16")
    Integer persona,

    @NotNull(message = "null일 수 없습니다.")
    Long universityId,

    @NotNull(message = "null일 수 없습니다.")
    @NotEmpty(message = "비어 있을 수 없습니다.")
    String majorName
) {

}

