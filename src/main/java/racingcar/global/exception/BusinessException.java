package racingcar.global.exception;

import lombok.Getter;
import racingcar.global.dto.BaseCode;

@Getter
public class BusinessException extends RuntimeException {

    private final BaseCode errorCode;

    public BusinessException(BaseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
