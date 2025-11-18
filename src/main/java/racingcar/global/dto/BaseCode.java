package racingcar.global.dto;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getStatus();
    String getMessage();
}
