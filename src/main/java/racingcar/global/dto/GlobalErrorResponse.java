package racingcar.global.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorResponse implements BaseCode {

    GLOBAL_ERROR_SAMPLE(HttpStatus.INTERNAL_SERVER_ERROR, "전역 오류 샘플"),
    REQUEST_BODY_INVALID(HttpStatus.BAD_REQUEST, "파라미터 유효성 검증 실패"),
    REQUEST_BODY_NOT_READABLE(HttpStatus.BAD_REQUEST, "요청 메시지 변환 실패 (누락 등)"),
    REQUEST_PARAM_REQUIRED(HttpStatus.BAD_REQUEST, "필수 요청 파라미터 누락"),
    METHOD_PARAM_INVALID(HttpStatus.BAD_REQUEST, "메서드 파라미터 유효성 검증 실패"),
    METHOD_PARAM_TYPE_INVALID(HttpStatus.BAD_REQUEST, "메서드 파라미터 타입 검증 실패"),
    HANDLER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 엔드포인트"),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "처리되지 않은 예외");

    private final HttpStatus status;
    private final String message;
}
