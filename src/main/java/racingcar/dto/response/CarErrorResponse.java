package racingcar.dto.response;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import racingcar.global.dto.BaseCode;

@RequiredArgsConstructor
public enum CarErrorResponse implements BaseCode {

    PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    CAR_NOT_FOUND(HttpStatus.NOT_FOUND, "자동차를 찾을 수 없습니다."),
    ONLY_HOST_ALLOWED(HttpStatus.FORBIDDEN, "해당 기능은 방장만 실행할 수 있습니다."),
    TOO_FEW_PARTICIPANT(HttpStatus.BAD_REQUEST, "자동차 경주는 두 명 이상 입장시 시작할 수 있습니다."),
    TOO_MANY_PARTICIPANT(HttpStatus.BAD_REQUEST, "자동차 경주 인원이 가득 찼습니다. 조금만 기다려주세요.");

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
