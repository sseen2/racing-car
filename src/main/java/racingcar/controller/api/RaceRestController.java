package racingcar.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import racingcar.dto.request.RaceEnterRequest;
import racingcar.dto.request.RaceLeaveRequest;
import racingcar.dto.request.RaceStartRequest;
import racingcar.dto.response.CarInfoResponse;
import racingcar.dto.response.success.RaceSuccess;
import racingcar.global.dto.ApiResponse;
import racingcar.service.RaceService;

@RestController
@RequestMapping("/api/race")
@RequiredArgsConstructor
@Tag(name = "레이스 REST API")
public class RaceRestController {

    private final RaceService raceService;

    @PostMapping("/enter")
    @Operation(summary = "자동차 경주 입장", description = "자동차 경주 게임 방에 입장합니다.")
    public ApiResponse<CarInfoResponse> enterRace(@RequestBody @Valid RaceEnterRequest request) {
        CarInfoResponse response = raceService.enterRace(request);
        return ApiResponse.success(RaceSuccess.ENTER_RACE, response);
    }

    @PostMapping("/start")
    @Operation(summary = "자동차 경주 시작", description = "시도 횟수를 입력받아 자동차 경주를 시작합니다.")
    public ApiResponse<Void> startRace(@RequestBody @Valid RaceStartRequest request) {
        raceService.startRace(request);
        return ApiResponse.success(RaceSuccess.START_RACE);
    }

    @PostMapping("/leave")
    @Operation(summary = "자동차 경주 나가기", description = "자동차 경주 게임 방에서 나갑니다.")
    public ApiResponse<Void> leaveRace(@RequestBody @Valid RaceLeaveRequest request) {
        raceService.leaveRace(request);
        return ApiResponse.success(RaceSuccess.LEAVE_RACE);
    }
}
