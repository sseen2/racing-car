package racingcar.dto.response.success;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import racingcar.global.dto.BaseCode;

@RequiredArgsConstructor
public enum CarSuccess implements BaseCode {

    REGISTER_CAR(HttpStatus.OK, "자동차 등록 성공."),
    GET_PARTICIPANTS(HttpStatus.OK, "자동차 경주 참여자 목록 조회 성공.");

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
