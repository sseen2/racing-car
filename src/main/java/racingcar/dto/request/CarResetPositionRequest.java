package racingcar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CarResetPositionRequest(

    @Schema(description = "자동차 이름")
    @NotBlank
    String carName
) {
}
