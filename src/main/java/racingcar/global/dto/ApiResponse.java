package racingcar.global.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    @JsonIgnore
    private final HttpStatus httpStatus;

    private final boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    public static <T> ApiResponse<T> success(BaseCode code) {
        return new ApiResponse<>(code.getStatus(), true, code.getMessage(), null);
    }

    public static <T> ApiResponse<T> success(BaseCode code, T data) {
        return new ApiResponse<>(code.getStatus(), true, code.getMessage(), data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, true, null, data);
    }

    public static <T> ApiResponse<T> fail(BaseCode code) {
        return new ApiResponse<>(code.getStatus(), false, code.getMessage(), null);
    }

    public static <T> ApiResponse<T> fail(BaseCode code, T data) {
        return new ApiResponse<>(code.getStatus(), false, code.getMessage(), data);
    }
}
