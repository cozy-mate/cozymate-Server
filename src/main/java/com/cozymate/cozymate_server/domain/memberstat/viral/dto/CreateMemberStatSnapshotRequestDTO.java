package com.cozymate.cozymate_server.domain.memberstat.viral.dto;

import com.cozymate.cozymate_server.domain.memberstat.memberstat.dto.request.LifestyleInput;
import com.cozymate.cozymate_server.domain.memberstat.memberstat.util.AnswerValueValid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;

@Builder(toBuilder = true)
public record CreateMemberStatSnapshotRequestDTO(
    @NotNull(message = "기상시간은 필수입니다.")
    @Min(0)
    @Max(23)
    Integer wakeUpTime,
    @NotNull(message = "수면시간은 필수입니다.")
    @Min(0)
    @Max(23)
    Integer sleepingTime,
    @NotNull(message = "소등시간은 필수입니다.")
    @Min(0)
    @Max(23)
    Integer turnOffTime,
    @NotEmpty(message = "흡연여부는 필수입니다.")
    @AnswerValueValid(questionKey = "smokingStatus")
    String smokingStatus,
    @NotEmpty(message = "잠버릇은 필수입니다.")
    @AnswerValueValid(questionKey = "sleepingHabits")
    List<String> sleepingHabits,
    @NotNull(message = "에어컨강도는 필수입니다.")
    @AnswerValueValid(questionKey = "coolingIntensity")
    String coolingIntensity,
    @NotNull(message = "히터강도는 필수입니다.")
    @AnswerValueValid(questionKey = "heatingIntensity")
    String heatingIntensity,
    @NotEmpty(message = "생활패턴은 필수입니다.")
    @AnswerValueValid(questionKey = "lifePattern")
    String lifePattern,
    @NotEmpty(message = "친밀도는 필수입니다.")
    @AnswerValueValid(questionKey = "intimacy")
    String intimacy,
    @NotEmpty(message = "물건공유는 필수입니다.")
    @AnswerValueValid(questionKey = "sharingStatus")
    String sharingStatus,
    @NotEmpty(message = "게임여부는 필수입니다.")
    @AnswerValueValid(questionKey = "gamingStatus")
    String gamingStatus,
    @NotEmpty(message = "전화여부는 필수입니다.")
    @AnswerValueValid(questionKey = "callingStatus")
    String callingStatus,
    @NotEmpty(message = "공부여부는 필수입니다.")
    @AnswerValueValid(questionKey = "studyingStatus")
    String studyingStatus,
    @NotEmpty(message = "섭취여부는 필수입니다.")
    @AnswerValueValid(questionKey = "eatingStatus")
    String eatingStatus,
    @NotEmpty(message = "청결예민도는 필수입니다.")
    @AnswerValueValid(questionKey = "cleannessSensitivity")
    String cleannessSensitivity,
    @NotEmpty(message = "소음예민도는 필수입니다.")
    @AnswerValueValid(questionKey = "noiseSensitivity")
    String noiseSensitivity,
    @NotEmpty(message = "청소빈도는 필수입니다.")
    @AnswerValueValid(questionKey = "cleaningFrequency")
    String cleaningFrequency,
    @NotEmpty(message = "음주빈도는 필수입니다.")
    @AnswerValueValid(questionKey = "drinkingFrequency")
    String drinkingFrequency,
    @NotEmpty(message = "성격은 필수입니다.")
    @AnswerValueValid(questionKey = "personalities")
    List<String> personalities,
    @NotBlank(message = "mbti는 필수입니다.")
    @AnswerValueValid(questionKey = "mbti")
    @Size(max = 4, min = 4)
    String mbti
) implements LifestyleInput {

}
