package racingcar.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CarRegisterRequest(

    @Schema(description = "자동차 이름")
    @NotBlank
    @Size(min = 1, max = 20, message = "자동차 이름 길이는 1~20 사이여야 합니다.")
    String carName,

    @Schema(description = "비밀번호")
    @NotBlank
    @Size(min = 1, max = 20, message = "비밀번호 길이는 1~20 사이여야 합니다.")
    String password
) {
}
