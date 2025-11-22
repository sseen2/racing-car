package racingcar.dto.response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import racingcar.global.dto.BaseCode;

@RequiredArgsConstructor
public enum RaceSuccessResponse implements BaseCode {

    START_RACE(HttpStatus.OK, "자동차 경주 시작 성공.");

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
