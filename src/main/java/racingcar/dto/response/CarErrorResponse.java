package racingcar.dto.response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import racingcar.global.dto.BaseCode;

@RequiredArgsConstructor
public enum CarErrorResponse implements BaseCode {

    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");

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
