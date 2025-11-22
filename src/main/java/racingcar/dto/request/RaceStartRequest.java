package racingcar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RaceStartRequest(

    @Schema(description = "자동차 이름")
    @NotBlank
    String carName,

    @Schema(description = "시도 횟수")
    @NotNull
    @Max(value = 10, message = "시도 횟수는 10회 이하로 입력해주세요.")
    int tryCount
) {
}
