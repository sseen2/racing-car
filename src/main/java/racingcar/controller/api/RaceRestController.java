package racingcar.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import racingcar.dto.request.RaceStartRequest;
import racingcar.dto.response.success.RaceSuccess;
import racingcar.global.dto.ApiResponse;
import racingcar.service.CarService;

@RestController
@RequestMapping("/api/race")
@RequiredArgsConstructor
public class RaceRestController {

    private final CarService carService;

    @PostMapping("/start")
    @Operation(summary = "자동차 경주 시작", description = "시도 횟수를 입력받아 자동차 경주를 시작합니다.")
    public ApiResponse<Void> startRace(@RequestBody @Valid RaceStartRequest request) {
        carService.startRace(request);
        return ApiResponse.success(RaceSuccess.START_RACE);
    }
}
