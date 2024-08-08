package com.cozymate.cozymate_server.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinRequestDTO {

        /**
         * {
         *     "name": "John Doe",
         *     "nickName": "johnny",
         *     "gender": "MALE",
         *     "birthday": "1990-01-01"
         *     "persona" : 1
         * }
         */

        @NotNull
        @NotEmpty
        private String name;

        @NotNull
        @NotEmpty
        private String nickName;

        @NotNull
        private String gender;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate birthday;

        @NotNull
        @Max(value = 16)
        @Min(value = 1)
        private Integer persona;
    }

}
