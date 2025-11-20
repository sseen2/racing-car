package racingcar.dto.response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import racingcar.global.dto.BaseCode;

@RequiredArgsConstructor
public enum CarSuccessResponse implements BaseCode {

    REGISTER_CAR(HttpStatus.OK, "자동차 등록 성공.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
