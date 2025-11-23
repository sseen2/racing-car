package racingcar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record RaceEnterRequest(

    @Schema(description = "자동차 이름")
    String carName
) {
}
