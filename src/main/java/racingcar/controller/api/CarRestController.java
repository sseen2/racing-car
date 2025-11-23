package racingcar.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import racingcar.dto.request.CarRegisterRequest;
import racingcar.dto.request.CarResetPositionRequest;
import racingcar.dto.response.CarInfoResponse;
import racingcar.dto.response.HistoryResponse;
import racingcar.dto.response.success.CarSuccess;
import racingcar.global.dto.ApiResponse;
import racingcar.service.CarService;
import racingcar.service.HistoryService;
import racingcar.service.WebSocketService;

@RestController
@RequestMapping("/api/car")
@RequiredArgsConstructor
@Tag(name = "자동차 REST API")
public class CarRestController {

    private final CarService carService;
    private final HistoryService historyService;
    private final WebSocketService webSocketService;

    @PostMapping("/register")
    @Operation(summary = "자동차 등록", description = "자동차가 없는 경우 자동차를 등록하고, 이미 등록된 경우 비밀번호를 확인 후 게임에 입장합니다.")
    public ApiResponse<CarInfoResponse> registerCar(@RequestBody @Valid CarRegisterRequest request) {
        CarInfoResponse response = carService.registerCar(request);
        carService.sendParticipants();
        webSocketService.sendLog(request.carName() + "님이 입장했습니다.");
        return ApiResponse.success(CarSuccess.REGISTER_CAR, response);
    }

    @GetMapping("/participants")
    @Operation(summary = "자동차 경주 참가자 목록 조회", description = "자동차 경주에 참가한 자동차 이름 목록을 조회합니다.")
    public ApiResponse<List<CarInfoResponse>> getParticipants() {
        List<CarInfoResponse> response = carService.updateParticipants();
        return ApiResponse.success(CarSuccess.GET_PARTICIPANTS, response);
    }

    @PostMapping("/reset/position")
    @Operation(summary = "자동차 포지션 초기화", description = "자동차의 포지션을 초기화합니다.")
    public ApiResponse<Void> resetPosition(@RequestBody @Valid CarResetPositionRequest request) {
        carService.resetPosition(request);
        return ApiResponse.success(CarSuccess.RESET_POSITION);
    }

    @GetMapping("/{carName}/history")
    @Operation(summary = "자동차 승패 기록 조회", description = "자동차의 승패 기록을 조회합니다.")
    public ApiResponse<HistoryResponse> getHistory(@PathVariable String carName) {
        HistoryResponse response = historyService.getHistory(carName);
        return ApiResponse.success(CarSuccess.GET_HISTORY, response);
    }
}
